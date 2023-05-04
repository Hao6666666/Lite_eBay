# Lite eBay Webapp

* Developed RESTful APIs (authentication, order, ticketing, payment) using Java, SpringBoot, and JPA.
* Automated the creation of Amazon Machine Images (AMIs) with Packer, reduced image build time by 50%.
* Implemented Infrastructure-as-Code on AWS with Terraform, deployed and managed EC2, VPC, RDS, and S3 resources.
* Configured a CI/CD pipeline on GitHub Actions with YAML, and conducted JUnit tests with MockMVC.
* Improved system scalability by over 70% through configuring AWS CloudWatch, Load Balancer, and Auto Scaling group.
* Streamlined cluster management with Kubernetes, Skaffold, and Docker.

&nbsp;
### Architecture Diagram

 ![aws_full](https://user-images.githubusercontent.com/42703011/92800898-211c7580-f383-11ea-9b4e-76c171fca750.png)


Tools and Technologies
----------------------
                          
| Infrastructure       | VPC, ELB, EC2, Route53, Cloud formation, Shell, Packer |
|----------------------|--------------------------------------------------------|
| Webapp               | Java, Spring Boot, MySQL, Maven                        |
| CI/CD                | Github Actions, AWS Code Deploy                        |
| Alerting and logging | statsd, Cloud Watch, SNS, SES, Lambda                  |
| Security             | SSL                                                    |

Infrastructure-setup
--------------------

-   Create the networking setup using cloud formation and aws cli
-   Create the required IAM policies and users
-   Setup Load Balancers, Route53, RDS, S3, SNS, SES, SSL 

#### Prerequisites

#### 1. Git clone this repository to your computer

> git@github.com:Hao6666666/Lite_eBay.git

#### 2. Install MySQL database to your computer

    https://dev.mysql.com/downloads/installer/

#### 3. Change MySQL database settings

    Find resources package and find the application.yml file

    Change MySQL database username and password as your username and password

#### 4. Run webapp

###### Steps to run web app

     1. After clone git repository to your local system and navigate to the project in Terminal using cd webapp
     2. Open IDEA
     3. run webapp

&nbsp;

# aws-infra (Infra-as-code)

&nbsp;
[Packer](https://packer.io/)
----------------------------

-   Implemented CI to build out an AMI and share it between organization
    on AWS
-   Created provisioners and bootstrapped the EC2 instance with required
    tools like Tomcat, JAVA, Python

[Terraform](https://terraform.io/)
----------------------------

-   Configured AWS cloud resources such as EC2, VPC, S3, RDS, Route53, 
    ELB, Auto-scalling group, etc.

#### Pre requisites

#### 1. Git clone this repository to your computer.

> git@github.com:Hao6666666/Lite_eBay.git

#### 2. Initiate terraform in your commond line.

    terraform init

#### 3. Plan terraform to AWS in your commond line.

    terraform plan -var-file="dev.tfvars"
    or
    terraform plan -var-file="demo.tfvars"

#### 4. Apply terraform in your commond line.

    terraform apply -var-file="dev.tfvars"
    or
    terraform apply -var-file="demo.tfvars"

#### 5. Destroy terraform in your commond line.

    terraform destroy -var-file="dev.tfvars"
    or
    terraform destroy -var-file="demo.tfvars"

#### 6. Command to import SSL certificate

> aws acm import-certificate --profile demo --certificate fileb://demo_tuffy666_me.crt --private-key fileb://private.key --certificate-chain fileb://demo_tuffy666_me.ca-bundle

&nbsp;

# CI/CD
* Using github actions configured CI/CD pipeline, the pipeline YAML file can be found in `.github/workflos`

# Running Tests
* Used mockito and junit for test case.
* Run WebappApplication test cases: open the webapp aplication on your IDE -> right click on webapp -> Run 'All Tests'

# Auto scaling groups
* Created auto scaling groups to scale to the application to handle the webtraffic and keep the costs low when traffic is low
* Created cloud watch alarms to scale up and scale down the EC2 instances


