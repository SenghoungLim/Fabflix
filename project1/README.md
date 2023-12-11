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
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 464                        | 396.407                             | 394.116                   | ran for 2m   |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 3661                       | 3786.64                             | 3785.71                   | ran for 2m   |
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | 4141                       | 4169.31                             | 4168.50                   | ran for 5m   |
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 3791                       | 3902.53                             | 3173.37                   | ran for 2m   |

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 212                        | 185.250                             | 183.518                   | ran for 2m   |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 888                        | 847.009                             | 846.794                   | ran for 5m   |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 889                        | 861.485                             | 824.863                   | ran for 2m   |



