# aws-infra

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
