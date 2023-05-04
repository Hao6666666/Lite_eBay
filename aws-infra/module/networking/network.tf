provider "aws" {
  region  = var.region
  profile = var.profile
}

# Create VPC
# terraform aws create vpc
resource "aws_vpc" "vpc" {
  cidr_block           = var.cidr
  instance_tenancy     = "default"
  enable_dns_hostnames = true

  tags = {
    Name = var.vpc_tag
  }
}

resource "aws_security_group" "application" {
  name_prefix = "application-security-group"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port = 22
    to_port   = 22
    protocol  = "tcp"
    # cidr_blocks = ["0.0.0.0/0"]
    security_groups = [aws_security_group.load_balancer.id]
  }

  # ingress {
  #   from_port   = 80
  #   to_port     = 80
  #   protocol    = "tcp"
  #   cidr_blocks = ["0.0.0.0/0"]
  # }

  # ingress {
  #   from_port   = 443
  #   to_port     = 443
  #   protocol    = "tcp"
  #   cidr_blocks = ["0.0.0.0/0"]
  # }

  ingress {
    from_port = 8080
    to_port   = 8080
    protocol  = "tcp"
    # cidr_blocks = ["0.0.0.0/0"]
    security_groups = [aws_security_group.load_balancer.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# resource "aws_instance" "ec2_instance" {
#   ami                         = var.ami_id
#   instance_type               = "t2.micro"
#   key_name                    = var.key_name
#   vpc_security_group_ids      = [aws_security_group.application.id]
#   subnet_id                   = aws_subnet.public-subnets[0].id
#   associate_public_ip_address = true

#   user_data = <<-EOF
#     #!/bin/bash
#     sudo chmod -v 777 /etc/bashrc

#     echo "export DB_HOST=${aws_db_instance.rds.endpoint}" >> /etc/bashrc
#     echo "export DB_NAME=${var.dbname}" >> /etc/bashrc
#     echo "export DB_USERNAME=${var.rdsUsername}" >> /etc/bashrc
#     echo "export DB_PASSWORD=${var.rdsPassword}" >> /etc/bashrc
#     echo "export BUCKET_NAME=${aws_s3_bucket.private_bucket.bucket}" >> /etc/bashrc
#     source /etc/bashrc

#     sudo systemctl daemon-reload
#     sudo systemctl start myapp.service
#     sudo systemctl enable myapp
#     sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl     -a fetch-config     -m ec2     -c file:/opt/aws/amazon-cloudwatch-agent/etc/cloudwatch-config.json     -s
#   EOF

#   root_block_device {
#     volume_size           = 50
#     volume_type           = "gp2"
#     delete_on_termination = true
#   }

#   tags = {
#     Name = "webapp-ec2-instance"
#   }

#   iam_instance_profile = aws_iam_instance_profile.ec2_profile.name
# }

# Define the IAM role
resource "aws_iam_role" "ec2_role" {
  name = "EC2-CSYE6225"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}


# Define the policy for accessing S3 resources
resource "aws_iam_policy" "WebAppS3" {
  name = "WebAppS3-policy"
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:Get*",
          "s3:List*",
          "s3:PutObject",
          "s3:DeleteObject",
        ]
        Resource = [
          "arn:aws:s3:::${aws_s3_bucket.private_bucket.bucket}",
          "arn:aws:s3:::${aws_s3_bucket.private_bucket.bucket}/*",
        ]
      },
    ]
  })
}

## Attach the WebAppS3 policy to the IAM role
resource "aws_iam_role_policy_attachment" "ec2_role_policy" {
  policy_arn = aws_iam_policy.WebAppS3.arn
  role       = aws_iam_role.ec2_role.name
}


# Define an IAM instance profile to attach to the EC2 instance
resource "aws_iam_instance_profile" "ec2_profile" {
  name = "EC2-CSYE6225-profile"
  role = aws_iam_role.ec2_role.name
}

# Create Internet Gateway and Attach it to VPC
# terraform aws create internet gateway
resource "aws_internet_gateway" "internet-gateway" {
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = var.IGW_tag
  }
}

# Create Public Subnets
# terraform aws create subnet
resource "aws_subnet" "public-subnets" {
  count                   = var.public_subnet_nums
  vpc_id                  = aws_vpc.vpc.id
  cidr_block              = cidrsubnet(var.cidr, 8, count.index + 1)
  availability_zone       = join("", [var.region, var.availability_zones[count.index]])
  map_public_ip_on_launch = true

  tags = {
    Name = "Public Subnet ${count.index + 1}"
  }
}

# Create Route Table and Add Public Route
# terraform aws create route table
resource "aws_route_table" "public-route-table" {
  vpc_id = aws_vpc.vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.internet-gateway.id
  }

  tags = {
    Name = var.public_route_table_tag
  }
}

# Associate Public Subnets to "Public Route Table"
# terraform aws associate subnet with route table
resource "aws_route_table_association" "public-subnets-route-table-association" {
  count          = var.public_subnet_nums
  subnet_id      = aws_subnet.public-subnets.*.id[count.index]
  route_table_id = aws_route_table.public-route-table.id
}


# Create Private Subnets
# terraform aws create subnet
resource "aws_subnet" "private-subnets" {
  count                   = var.private_subnet_nums
  vpc_id                  = aws_vpc.vpc.id
  cidr_block              = cidrsubnet(var.cidr, 8, count.index + var.public_subnet_nums + 1)
  availability_zone       = join("", [var.region, var.availability_zones[count.index]])
  map_public_ip_on_launch = false

  tags = {
    Name = "Private Subnet ${count.index + var.public_subnet_nums + 1}"
  }
}

# Create Route Table and Add Private Route
# terraform aws create route table
resource "aws_route_table" "private-route-table" {
  vpc_id = aws_vpc.vpc.id

  tags = {
    Name = var.private_route_table_tag
  }
}

# Associate Private Subnets to "Private Route Table"
# terraform aws associate subnet with route table
resource "aws_route_table_association" "private-subnets-route-table-association" {
  count          = var.private_subnet_nums
  subnet_id      = aws_subnet.private-subnets.*.id[count.index]
  route_table_id = aws_route_table.private-route-table.id
}


resource "random_string" "bucket_name" {
  length  = 8
  special = false
}

resource "aws_s3_bucket" "private_bucket" {
  bucket        = "${lower(random_string.bucket_name.result)}-${var.profile}"
  force_destroy = true
}

resource "aws_s3_bucket_server_side_encryption_configuration" "private_bucket_sse" {
  bucket = aws_s3_bucket.private_bucket.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}

# resource "aws_s3_bucket_acl" "private_bucket_acl" {
#   bucket = aws_s3_bucket.private_bucket.id

#   acl = "private"
# }

resource "aws_s3_bucket_lifecycle_configuration" "private_bucket_lifecycle" {
  bucket = aws_s3_bucket.private_bucket.id

  rule {
    id     = "standard-to-ia-transition-rule"
    status = "Enabled"
    transition {
      days          = 30
      storage_class = "STANDARD_IA"
    }
  }
}

# Create a private subnet group for RDS instances
resource "aws_db_subnet_group" "private_db_subnet_group" {
  name       = "private_db_subnet_group"
  subnet_ids = aws_subnet.private-subnets.*.id
}

# Create a security group for RDS instance
resource "aws_security_group" "database" {
  name_prefix = "database-security-group"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.application.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_db_parameter_group" "mysql" {
  name_prefix = "mysql"
  family      = "mysql8.0"

  parameter {
    name  = "max_connections"
    value = "100"
  }

  parameter {
    name  = "character_set_server"
    value = "utf8"
  }
}

# Create an RDS instance
resource "aws_db_instance" "rds" {
  identifier           = var.dbname
  engine               = "mysql"
  engine_version       = "8.0.23"
  instance_class       = "db.t3.micro"
  allocated_storage    = 5
  storage_type         = "gp2"
  storage_encrypted    = true
  publicly_accessible  = false
  multi_az             = false
  skip_final_snapshot  = true
  db_subnet_group_name = aws_db_subnet_group.private_db_subnet_group.name

  vpc_security_group_ids = [aws_security_group.database.id]

  kms_key_id = aws_kms_key.rds_key.arn

  # Set master username and password
  username = var.rdsUsername
  password = var.rdsPassword

  # Set the RDS instance parameter group
  parameter_group_name = aws_db_parameter_group.mysql.id
}

data "aws_iam_policy" "cloudwatch_agent_server_policy" {
  arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
}

resource "aws_iam_role_policy_attachment" "attach_cloudwatch_agent_server_policy" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = data.aws_iam_policy.cloudwatch_agent_server_policy.arn
}

resource "aws_route53_record" "dns-record" {
  zone_id = var.dns_zone_id
  name    = ""
  type    = "A"
  lifecycle {
    create_before_destroy = true
  }
  alias {
    name                   = aws_lb.lb.dns_name
    zone_id                = aws_lb.lb.zone_id
    evaluate_target_health = true
  }
}

data "template_file" "user_data" {

  template = <<-EOF
    #!/bin/bash
    sudo chmod -v 777 /etc/bashrc

    echo "export DB_HOST=${aws_db_instance.rds.endpoint}" >> /etc/bashrc
    echo "export DB_NAME=${var.dbname}" >> /etc/bashrc
    echo "export DB_USERNAME=${var.rdsUsername}" >> /etc/bashrc
    echo "export DB_PASSWORD=${var.rdsPassword}" >> /etc/bashrc
    echo "export BUCKET_NAME=${aws_s3_bucket.private_bucket.bucket}" >> /etc/bashrc
    source /etc/bashrc

    sudo systemctl daemon-reload
    sudo systemctl start myapp.service
    sudo systemctl enable myapp
    sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl     -a fetch-config     -m ec2     -c file:/opt/aws/amazon-cloudwatch-agent/etc/cloudwatch-config.json     -s
  EOF
}

resource "aws_launch_template" "lt" {
  image_id      = var.ami_id
  instance_type = "t2.micro"
  key_name      = var.key_name
  name          = "launch_config"

  iam_instance_profile {
    name = aws_iam_instance_profile.ec2_profile.name
  }

  network_interfaces {
    associate_public_ip_address = true
    security_groups             = [aws_security_group.application.id]
    subnet_id                   = aws_subnet.public-subnets[0].id
  }

  block_device_mappings {
    device_name = "/dev/xvda"
    ebs {
      delete_on_termination = true
      volume_type           = "gp2"
      volume_size           = 50
      encrypted             = true
      kms_key_id            = aws_kms_key.ebs_key.arn
    }
  }

  user_data = base64encode(data.template_file.user_data.rendered)
}

resource "aws_security_group" "load_balancer" {
  name_prefix = "load_balancer_security_group"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_autoscaling_group" "ec2_autoscaling_group" {
  name                = "ec2-asg"
  min_size            = 1
  max_size            = 3
  desired_capacity    = 1
  vpc_zone_identifier = [aws_subnet.public-subnets[0].id]

  tag {
    key                 = "csye6225"
    value               = "ec2-instance"
    propagate_at_launch = true
  }

  target_group_arns = [aws_lb_target_group.target_group.arn]

  launch_template {
    id      = aws_launch_template.lt.id
    version = "$Latest"
  }
}

# Scale up policy
resource "aws_autoscaling_policy" "scale_up" {
  name                   = "asg-scale-up"
  autoscaling_group_name = aws_autoscaling_group.ec2_autoscaling_group.name
  adjustment_type        = "ChangeInCapacity"
  scaling_adjustment     = 1 # increase by 1
  cooldown               = 60
  policy_type            = "SimpleScaling"
}

# Scale down policy
resource "aws_autoscaling_policy" "scale_down" {
  name                   = "asg-scale-down"
  autoscaling_group_name = aws_autoscaling_group.ec2_autoscaling_group.name
  adjustment_type        = "ChangeInCapacity"
  scaling_adjustment     = -1 # decrease by 1
  cooldown               = 60
  policy_type            = "SimpleScaling"
}

# Scale up alarm
resource "aws_cloudwatch_metric_alarm" "scale_up_alarm" {
  alarm_name          = "scale-up-alarm"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  metric_name         = "CPUUtilization"
  namespace           = "AWS/EC2"
  period              = 60
  statistic           = "Average"
  threshold           = 5

  dimensions = {
    AutoScalingGroupName = aws_autoscaling_group.ec2_autoscaling_group.name
  }

  alarm_description = "Alarm CPU exceeds 5%"
  alarm_actions     = [aws_autoscaling_policy.scale_up.arn]
}

# Scale down alarm
resource "aws_cloudwatch_metric_alarm" "scale_down_alarm" {
  alarm_name          = "scale-down-alarm"
  comparison_operator = "LessThanThreshold"
  evaluation_periods  = 1
  metric_name         = "CPUUtilization"
  namespace           = "AWS/EC2"
  period              = 60
  statistic           = "Average"
  threshold           = 3

  dimensions = {
    AutoScalingGroupName = aws_autoscaling_group.ec2_autoscaling_group.name
  }

  alarm_description = "Alarm CPU falls below 3%"
  alarm_actions     = [aws_autoscaling_policy.scale_down.arn]
}

resource "aws_lb" "lb" {
  name               = "lb"
  internal           = false
  load_balancer_type = "application"
  subnets            = [for subnet in aws_subnet.public-subnets : subnet.id]

  security_groups = [aws_security_group.load_balancer.id]

  tags = {
    Name = "lb"
  }
}

resource "aws_lb_target_group" "target_group" {
  name_prefix = "tg"

  port     = 8080
  protocol = "HTTP"

  vpc_id      = aws_vpc.vpc.id
  target_type = "instance"

  health_check {
    path = "/healthz"
  }
}

# Add the target group as a listener rule to the HTTPS listener
resource "aws_lb_listener_rule" "https" {
  listener_arn = aws_lb_listener.https_listener.arn

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.target_group.arn
  }

  condition {
    host_header {
      values = ["${var.profile}.tuffy666.me"]
    }
  }
}

resource "aws_lb_listener" "https_listener" {
  load_balancer_arn = aws_lb.lb.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = var.certificate_arn
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.target_group.arn
  }
}

# resource "aws_lb_listener_rule" "http" {
#   listener_arn = aws_lb_listener.http_listener.arn

#   action {
#     type             = "forward"
#     target_group_arn = aws_lb_target_group.target_group.arn
#   }

#   condition {
#     host_header {
#       values = ["${var.profile}.tuffy666.me"]
#     }
#   }
# }

# resource "aws_lb_listener" "http_listener" {
#   load_balancer_arn = aws_lb.lb.arn
#   port              = "80"
#   protocol          = "HTTP"
#   default_action {
#     type             = "forward"
#     target_group_arn = aws_lb_target_group.target_group.arn
#   }
# }

resource "aws_kms_key" "ebs_key" {
  description             = "KMS key for ebs"
  deletion_window_in_days = 10
  policy = jsonencode(
    {
      "Id" : "ebs_key-policy",
      "Version" : "2012-10-17",
      "Statement" : [
        {
          "Sid" : "Enable IAM user permission",
          "Effect" : "Allow",
          "Principal" : {
            "AWS" : "arn:aws:iam::${var.account_id}:root"
          },
          "Action" : "kms:*",
          "Resource" : "*",
        },
        {
          "Sid" : "Enable kms access for auto scaling",
          "Effect" : "Allow",
          "Principal" : {
            "AWS" : "arn:aws:iam::${var.account_id}:role/aws-service-role/autoscaling.amazonaws.com/AWSServiceRoleForAutoScaling"
          },
          "Action" : "kms:*",
          "Resource" : "*",
        }
      ]
    }
  )

}


resource "aws_kms_key" "rds_key" {
  description = "RDS encryption"
  policy      = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "Enable IAM User Permissions",
      "Effect": "Allow",
      "Principal": {"AWS": "arn:aws:iam::${var.account_id}:root"},
      "Action": "kms:*",
      "Resource": "*"
    },
    {
      "Sid": "Allow RDS encryption",
      "Effect": "Allow",
      "Principal": {"Service": "rds.amazonaws.com"},
      "Action": [
        "kms:Encrypt*",
        "kms:Decrypt*",
        "kms:ReEncrypt*",
        "kms:GenerateDataKey*",
        "kms:DescribeKey"
      ],
      "Resource": "*"
    }
  ]
}
EOF
}

resource "aws_kms_alias" "ebs_alias" {
  name          = "alias/ebs_key"
  target_key_id = aws_kms_key.ebs_key.key_id
}

resource "aws_kms_alias" "rds_alias" {
  name          = "alias/rds_key"
  target_key_id = aws_kms_key.rds_key.key_id
}
