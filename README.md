## 개요
* 첫 회사(BTB Solution) 재직 시절에 운영하고 있던 서버가 살아있는지 확인하기 위해 만든 Monitoring 데몬들이다.
* Netty를 공부하던때라 공부할 겸 Netty로 만들었다.
* 각각의 모니터링 데몬들이 주기적으로 서버를 찔러보고 응답이 없으면 MonitoringMailManager로 이벤트를 준다.<br/>
  MonitoringMailManager는 이벤트를 받으면 나에게 메일을 보내도록 되어있다.

## 기술스택
* Java
* Netty
