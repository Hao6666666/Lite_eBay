variable "region" {
  default = "us-west-2"
  type    = string
}

variable "key_name" {
  default = "hao"
  type    = string
}

variable "profile" {
  default = "dev"
  type    = string
}

variable "cidr" {
  default = "10.0.0.0/16"
  type    = string
}

variable "vpc_tag" {
  default = "CSYE6225 VPC"
  type    = string
}

variable "IGW_tag" {
  default = "CSYE6225 Public IGW"
  type    = string
}

variable "public_subnet_nums" {
  default = 3
  type    = number
}

variable "private_subnet_nums" {
  default = 3
  type    = number
}

variable "availability_zones" {
  default = ["a", "b", "c", "d"]
  type    = list(string)
}

variable "public_route_table_tag" {
  default = "Public Route Table"
  type    = string
}

variable "private_route_table_tag" {
  default = "Private Route Table"
  type    = string
}

variable "dns_zone_id" {
  type = string
}

variable "account_id" {
  type = string
}

variable "certificate_arn" {
  type = string
}
