# 一、SSE

## 1、什么是 SSE
SSE （ Server-sent Events ）是 WebSocket 的一种轻量代替方案，使用 HTTP 协议。

严格地说，HTTP 协议是没有办法做服务器推送的，但是当服务器向客户端声明接下来要发送流信息时，客户端就会保持连接打开，SSE 使用的就是这种原理。

SSE 是 Server-Sent Events 的简称， 是一种服务器端到客户端(浏览器)的单项消息推送。

相比于 WebSocket，SSE 简单不少，服务器端和客户端工做量都要小不少、简单不少，同时实现的功能也有局限。

## 2、SSE与WebSocket的区别

WebSocket是全双工通道，可以双向通信，功能更强；SSE是单向通道，只能服务器向浏览器端发送。如果客户端需要向服务器发送消息，则需要一个新的 HTTP 请求。 这对比 WebSocket
的双工通道来说，会有更大的开销。
WebSocket是一个新的协议，需要服务器端支持；SSE则是部署在 HTTP协议之上的，现有的服务器软件都支持。
SSE是一个轻量级协议，相对简单；WebSocket是一种较重的协议，相对复杂。
SSE默认支持断线重连，WebSocket则需要额外部署。
SSE支持自定义发送的数据类型。
SSE不支持CORS，参数url就是服务器网址，必须与当前网页的网址在同一个网域（domain），而且协议和端口都必须相同。WebSocket支持

## 3、SSE（Server-sent Events）在HTML 5中的技术规范和定义

Server-sent Events 规范是 HTML 5 规范的一个组成部分，具体的规范文档见参考资源。该规范比较简单，主要由两个部分组成：
第一个部分是服务器端与浏览器端之间的通讯协议，
第二部分则是在浏览器端可供 JavaScript 使用的 EventSource 对象。

通讯协议是基于纯文本的简单协议。服务器端的响应的内容类型是“text/event-stream”。响应文本的内容可以看成是一个事件流，由不同的事件所组成。
每个事件由类型和数据两部分组成，同时每个事件可以有一个可选的标识符。不同事件的内容之间通过仅包含回车符和换行符的空行（“\r\n”）来分隔。每个事件的数据可能由多行组成。

# 二、使用
项目中引用此依赖
```xml
    <dependency>
      <groupId>com.say.common</groupId>
      <artifactId>sse</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>
```