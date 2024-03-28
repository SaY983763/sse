package com.say.common.sse.server;

import cn.hutool.core.util.ObjectUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author zrs
 */
@UtilityClass
public class SseEmitterServer {

  private static final Logger logger = LoggerFactory.getLogger(SseEmitterServer.class);

  /**
   * 使用map对象，便于根据requestIdId来获取对应的SseEmitter，或者放redis里面
   */
  private static final Map<String, SseEmitter> SSE_EMITTER_MAP = new ConcurrentHashMap<>();

  /**
   * 用于创建连接
   *
   * @param requestId 请求唯一标识
   * @param timeOut 超时时间，单位：s
   * @return sse连接
   */
  public static SseEmitter connect(String requestId, Long timeOut) {
    // 假如同一个用户多终端同时在线，仅保留最后一个终端
    if (SSE_EMITTER_MAP.containsKey(requestId)) {
      SseEmitter emitter = SSE_EMITTER_MAP.remove(requestId);
      try {
        emitter.send("您的连接已被顶下线");
      } catch (IOException e) {
        logger.info("该用户可能已下线");
      } finally {
        logger.info("连接已存在, 删除[{}]连接", requestId);
        emitter.complete();
      }
    }
    // 设置超时时间，0表示不过期。不设置默认30秒，超过时间未完成会抛出异常：AsyncRequestTimeoutException
    // 这里根据业务设置10分钟
    SseEmitterUTF8 sseEmitter = new SseEmitterUTF8(
        ObjectUtil.isNotEmpty(timeOut) && timeOut > 0 ? timeOut * 1000L : 600 * 1000L);
    // 注册回调
    sseEmitter.onCompletion(completionCallBack(requestId));
    sseEmitter.onError(errorCallBack(requestId));
    sseEmitter.onTimeout(timeoutCallBack(requestId));
    SSE_EMITTER_MAP.put(requestId, sseEmitter);
    logger.info("创建新的sse连接，当前连接：{}", requestId);
    return sseEmitter;
  }

  /**
   * 创建连接并返回 SseEmitter
   *
   * @param requestId 唯一ID
   * @return SseEmitter
   */
  public static SseEmitter connect(String requestId) {
    return connect(requestId, null);
  }

  /**
   * 给指定用户发送信息
   *
   * @param requestId 唯一ID
   * @param message 消息体
   * @return true：成功；false：失败
   */
  public static boolean sendMessage(String requestId, String message) {
    boolean flag = false;
    if (SSE_EMITTER_MAP.containsKey(requestId)) {
      try {
        SSE_EMITTER_MAP.get(requestId).send(message);
        flag = true;
      } catch (Exception e) {
        logger.error("[{}]推送异常:{}", requestId, e.getMessage(), e);
      }
    }/* else {
			logger.warn("与[{}]连接不存在", requestId);
		}*/
    return flag;
  }

  /**
   * 群发消息
   *
   * @param wsInfo 消息
   * @param requestIds 接收消息的ids
   */
  public static void batchSendMessage(String wsInfo, List<String> requestIds) {
    requestIds.forEach(requestId -> sendMessage(wsInfo, requestId));
  }

  /**
   * 群发所有人
   *
   * @param wsInfo 消息
   */
  public static void batchSendMessage(String wsInfo) {
    SSE_EMITTER_MAP.forEach((k, v) -> {
      try {
        v.send(wsInfo, MediaType.APPLICATION_JSON);
      } catch (IOException e) {
        logger.error("[{}]推送异常:{}", k, e.getMessage());
        remove(k);
      }
    });
  }

  /**
   * 移除连接
   *
   * @param requestId 唯一iD
   */
  public static void remove(String requestId) {
    SseEmitter sseEmitter = SSE_EMITTER_MAP.remove(requestId);
    if (ObjectUtil.isNotEmpty(sseEmitter)) {
      sseEmitter.complete();
      logger.info("中断连接[{}]", requestId);
    }
  }

  /**
   * 获取当前连接信息
   *
   * @return 连接信息
   */
  public static List<String> getRequestIds() {
    return new ArrayList<>(SSE_EMITTER_MAP.keySet());
  }

  /**
   * 获取当前连接数量
   *
   * @return 连接数量
   */
  public static int getConnectCount() {
    return SSE_EMITTER_MAP.size();
  }

  private static Runnable completionCallBack(String requestId) {
    return () -> {
      logger.info("结束连接[{}]", requestId);
      remove(requestId);
    };
  }

  private static Runnable timeoutCallBack(String requestId) {
    return () -> {
      logger.info("连接超时[{}]", requestId);
      remove(requestId);
    };
  }

  private static Consumer<Throwable> errorCallBack(String requestId) {
    return throwable -> {
      logger.info("连接异常[{}]", requestId);
      remove(requestId);
    };
  }

}
