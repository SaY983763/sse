package com.say.common.sse.service;

import com.alibaba.fastjson.JSON;
import com.say.common.sse.vo.SseMessageVO;
import com.say.common.sse.server.SseEmitterServer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author zrs
 */
@Service
@RequiredArgsConstructor
public class SseService {

  /**
   * 用于创建连接
   *
   * @param requestId 请求唯一标识
   * @param timeOut 超时时间，单位：s
   * @return sse连接
   */
  public SseEmitter connect(String requestId, Long timeOut) {
    return SseEmitterServer.connect(requestId, timeOut);
  }

  /**
   * 用于创建连接
   *
   * @param requestId 请求唯一标识
   * @return sse连接
   */
  public SseEmitter connect(String requestId) {
    return SseEmitterServer.connect(requestId);
  }

  /**
   * 推送给所有
   *
   * @param sseMessageVO 消息体
   * @return return
   */
  public String push(SseMessageVO sseMessageVO) {
    SseEmitterServer.batchSendMessage(JSON.toJSONString(sseMessageVO));
    return "Sse 推送消息给所有";
  }

  /**
   * 发送给单个
   *
   * @param requestId 被推送的ID
   * @return true：成功；false：失败
   */
  public Boolean pushOne(String requestId, @RequestBody SseMessageVO sseMessageVO) {
    return SseEmitterServer.sendMessage(requestId, JSON.toJSONString(sseMessageVO));
  }

  /**
   * 关闭连接
   */
  public String close(String requestId) {
    SseEmitterServer.remove(requestId);
    return "连接关闭";
  }

}
