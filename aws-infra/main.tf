module "network" {
  ami_id                  = "ami-071d924fc39e16238"
  key_name                = var.key_name
  region                  = "us-west-2"
  profile                 = var.profile
  source                  = "./module/networking"
  cidr                    = "10.0.0.0/16"
  vpc_tag                 = var.vpc_tag
  IGW_tag                 = var.IGW_tag
  public_subnet_nums      = 3
  private_subnet_nums     = 3
  availability_zones      = ["a", "b", "c", "d"]
  public_route_table_tag  = var.public_route_table_tag
  private_route_table_tag = var.private_route_table_tag
  dns_zone_id             = var.dns_zone_id
  account_id              = var.account_id
  certificate_arn         = var.certificate_arn
}
