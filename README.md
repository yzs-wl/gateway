# gateway-project
Spring Gateway Project,Including CircuitBreaker and RateLimiter Components 

---

1.  gateway.native  
2.  gateway.server  
3.  gateway.test      

---
# step-by-step

1.  Start the nacos server   
2.  Launch native project 
3.  Start the test project
4.  Test by entering the testing address through Postman or browser


---
# reminder

Building gateway native project requires GraalVM CE 17 and Visual Studio 2022 community 

1.  mvn -Pnative native:compile

---
# test
1.  http://127.0.0.1:7777/test/limit1
2.  http://127.0.0.1:7777/test/limit2
3.  http://127.0.0.1:7777/test/timeout1
4.  http://127.0.0.1:7777/test/timeout2