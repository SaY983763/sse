package com.say.common.sse.server;

import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 解决中文乱码，设置UTF8编码
 *
 * @author zrs
 */
public class SseEmitterUTF8 extends SseEmitter {

  public SseEmitterUTF8(Long timeout) {
    super(timeout);
  }

  @Override
  protected void extendResponse(ServerHttpResponse outputMessage) {
    super.extendResponse(outputMessage);

    HttpHeaders headers = outputMessage.getHeaders();
    headers.setContentType(new MediaType(MediaType.TEXT_EVENT_STREAM, StandardCharsets.UTF_8));
  }
}
