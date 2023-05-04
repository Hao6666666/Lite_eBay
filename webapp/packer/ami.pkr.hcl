variable "aws_region" {
  type    = string
  default = "us-west-2"
}

variable "ssh_username" {
  type    = string
  default = "ec2-user"
}

variable "source_ami" {
  type    = string
  default = "ami-0f1a5f5ada0e7da53" # Amazon Linux 2 LTS
}

variable "subnet_id" {
  type    = string
  default = "subnet-0fe2e538cbace8fa0"
}

source "amazon-ebs" "my-ami" {
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  instance_type   = "t2.micro"
  region          = var.aws_region
  source_ami      = var.source_ami
  ssh_username    = var.ssh_username
  subnet_id     = var.subnet_id
  ami_users = ["885506167307", "517179388326"]


  launch_block_device_mappings {
    device_name = "/dev/xvda"
    volume_size = 50
    volume_type = "gp2"
    delete_on_termination = true
  }
}

build {
  sources = ["source.amazon-ebs.my-ami"]

  provisioner "shell" {
    script = "setup.sh"
  }
  provisioner "file" {
    source = "launch.sh"
    destination = "/opt/deployment/launch.sh"
  }

  provisioner "shell" {
    inline = [ "sudo chmod +x /opt/deployment/launch.sh",
      "sudo cp /opt/deployment/launch.sh /var/lib/cloud/scripts/per-boot/launch.sh"]
  }
  provisioner "shell" {
    script = "mariadb.sh"
  }

  provisioner "file" {
    source      = "../target/CSYE6225-0.0.1-SNAPSHOT.jar"
    destination = "/opt/deployment/app.jar"
  }

  provisioner "shell" {
    script = "systemd.sh"
  }

  provisioner "file" {
    source      = "cloudwatch-config.json"
    destination = "/opt/aws/amazon-cloudwatch-agent/etc/cloudwatch-config.json"
  }

  provisioner "shell" {
    inline = [
      "sudo systemctl start amazon-cloudwatch-agent.service",
      "sudo systemctl enable amazon-cloudwatch-agent.service"
    ]
  }
}
