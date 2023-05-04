variable "cidr" {
  type = string
}

variable "region" {
  type = string
}

variable "ami_id" {
  type = string
}

variable "key_name" {
  type = string
}

variable "profile" {
  type = string
}

variable "vpc_tag" {
  type = string
}

variable "IGW_tag" {
  type = string
}

variable "public_subnet_nums" {
  type = number
}

variable "private_subnet_nums" {
  type = number
}

variable "availability_zones" {
  type = list(string)
}

variable "public_route_table_tag" {
  type = string
}

variable "private_route_table_tag" {
  type = string
}

variable "rdsUsername" {
  type    = string
  default = "csye6225"
}

variable "rdsPassword" {
  type    = string
  default = "rootpassword"
}

variable "dbname" {
  type    = string
  default = "csye6225"
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

# variable "demo_zone_id" {
#   type = string
#   default = "Z001722746ABGCGVW0HH"
# }

# variable "dev_zone_id" {
#   type = string
#   default = "Z092382115ES4SUY9JV04"
# }
# variable "root_zone_id" {
#   type = string
#   default = "Z0515641965JI2Z4DI6L"
# }
