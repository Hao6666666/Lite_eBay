# Lite eBay Webapp

* Developed RESTful APIs (authentication, order, ticketing, payment) using Java, SpringBoot, and JPA.
* Automated the creation of Amazon Machine Images (AMIs) with Packer, reduced image build time by 50%.
* Implemented Infrastructure-as-Code on AWS with Terraform, deployed and managed EC2, VPC, RDS, and S3 resources.
* Configured a CI/CD pipeline on GitHub Actions with YAML, and conducted JUnit tests with MockMVC.
* Improved system scalability by over 70% through configuring AWS CloudWatch, Load Balancer, and Auto Scaling group.
* Streamlined cluster management with Kubernetes, Skaffold, and Docker.

&nbsp;

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
