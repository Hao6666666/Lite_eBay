# Lite eBay Webapp

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
