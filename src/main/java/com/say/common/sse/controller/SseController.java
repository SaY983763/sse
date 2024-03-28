package com.say.common.sse.controller;

import com.say.common.sse.service.SseService;
import com.say.common.sse.vo.SseMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author zrs
 */
@CrossOrigin //解决跨域
@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseController {

  private final SseService sseService;

  /**
   * 用于创建连接
   *
   * @param requestId 请求唯一标识
   * @return sse连接
   */
  @GetMapping("/connect/{requestId}")
  public SseEmitter connect(@PathVariable String requestId) {
    return sseService.connect(requestId);
  }

  /**
   * 用于创建连接
   *
   * @param requestId 请求唯一标识
   * @param timeOut 超时时间，单位：s
   * @return sse连接
   */
  @GetMapping("/connect_v2/{requestId}")
  public SseEmitter connect(@PathVariable String requestId,
      @RequestParam("timeOut") Long timeOut) {
    return sseService.connect(requestId, timeOut);
  }

  /**
   * 推送给所有
   *
   * @param sseMessageVO 消息体
   * @return return
   */
  @PostMapping("/push_all")
  public ResponseEntity<String> push(@RequestBody SseMessageVO sseMessageVO) {
    return ResponseEntity.ok(sseService.push(sseMessageVO));
  }

  /**
   * 按照请求标识发送
   *
   * @param requestId 请求唯一标识
   * @return return
   */
  @PostMapping("/push_one/{requestId}")
  public ResponseEntity<String> pushOne(@PathVariable(name = "requestId") String requestId,
      @RequestBody SseMessageVO sseMessageVO) {
    return ResponseEntity.ok(sseService.pushOne(requestId, sseMessageVO) ? "Sse 推送消息给"
        + requestId + "成功！" : "Sse 推送消息给" + requestId + "失败！");
  }

  /**
   * 关闭连接
   */
  @GetMapping("/close/{requestId}")
  public ResponseEntity<String> close(@PathVariable("requestId") String requestId) {
    return ResponseEntity.ok(sseService.close(requestId));
  }


}
