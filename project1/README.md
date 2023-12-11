# CS122B Project 4
- [Demo Video Link](https://youtu.be/79ctHj10tjg)
## Member Distribution
  - Senghoung Lim
    - Task 2 and Demo
  - Thien Toan Vu
    - Task 1
## Special Instruction
- NA

# General
  - #### Team#:
  
  - #### Names:
  
  - #### Project 5 Video Demo Link:

  - #### Instruction of deployment:

  - #### Collaborations and Work Distribution:


# Connection Pooling
  - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
  
  - #### Explain how Connection Pooling is utilized in the Fabflix code.
  
  - #### Explain how Connection Pooling works with two backend SQL.
    

# Master/Slave
  - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.

  - #### How read/write requests were routed to Master/Slave SQL?
    

# JMeter TS/TJ Time Logs
  - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.

# JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          |<img width="500" height="200" alt="SingleHTTP1" src="https://github.com/uci-jherold2-fall23-cs122b/2023-fall-cs122b-jake/assets/67763741/888c4f6c-66de-49a8-a5f0-e4b939ba27ed">| 464                        | 396.407                             | 394.116                   | ran for 2m   |
| Case 2: HTTP/10 threads                        |<img width="500" height="200" alt="SingleHTTP10" src="https://github.com/uci-jherold2-fall23-cs122b/2023-fall-cs122b-jake/assets/67763741/1a573bc6-a6dc-4fb0-a3fb-94982bdf4261">| 3661                       | 3786.64                             | 3785.71                   | ran for 2m   |
| Case 3: HTTPS/10 threads                       |<img width="500" height="200" alt="SingleHTTPS10" src="https://github.com/uci-jherold2-fall23-cs122b/2023-fall-cs122b-jake/assets/67763741/ea7aa2d9-f6d5-49c0-9382-bc6469b33dc7">| 4141                       | 4169.31                             | 4168.50                   | ran for 5m   |
| Case 4: HTTP/10 threads/No connection pooling  |<img width="500" height="200" alt="SingleHTTP10NoPooling" src="https://github.com/uci-jherold2-fall23-cs122b/2023-fall-cs122b-jake/assets/67763741/08d05c4d-17ac-4815-9a30-98b0f8239a2f">
  | 3791        | 3902.53       | 3173.37                   | ran for 2m   |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          |<img width="500" height="200" alt="ScaledHTTP1" src="https://github.com/uci-jherold2-fall23-cs122b/2023-fall-cs122b-jake/assets/67763741/d0d835a1-e8e2-4166-b1a3-a6744f4fb181">| 212                        | 185.250                             | 183.518                   | ran for 2m   |
| Case 2: HTTP/10 threads                        |<img width="500" height="200" alt="ScaledHTTP10" src="https://github.com/uci-jherold2-fall23-cs122b/2023-fall-cs122b-jake/assets/67763741/68e5d8d4-6dc7-49c6-aa23-5ac33d64c63c">| 888                        | 847.009                             | 846.794                   | ran for 5m   |
| Case 3: HTTP/10 threads/No connection pooling  |<img width="500" height="200" alt="ScaledHTTP10NoPooling" src="https://github.com/uci-jherold2-fall23-cs122b/2023-fall-cs122b-jake/assets/67763741/b29699b9-695c-441d-9fa6-524af4b0cd42">| 889                        | 861.485                             | 824.863                   | ran for 2m   |



