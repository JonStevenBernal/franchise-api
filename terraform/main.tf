resource "mongodbatlas_project" "franchise_project" {
  name   = var.project_name
  org_id = var.atlas_org_id
}

resource "mongodbatlas_advanced_cluster" "franchise_cluster" {
  project_id   = mongodbatlas_project.franchise_project.id
  name         = var.cluster_name
  cluster_type = "REPLICASET"

  replication_specs = [
    {
      region_configs = [
        {
          provider_name         = "TENANT"
          backing_provider_name = "AWS"
          region_name           = var.aws_region
          priority              = 7
          electable_specs = {
            instance_size = "M0"
          }
        }
      ]
    }
  ]
}

resource "mongodbatlas_database_user" "franchise_db_user" {
  username           = var.db_username
  password           = var.db_password
  project_id         = mongodbatlas_project.franchise_project.id
  auth_database_name = "admin"

  roles {
    role_name     = "readWrite"
    database_name = var.db_name
  }
}

resource "mongodbatlas_project_ip_access_list" "allow_access" {
  project_id = mongodbatlas_project.franchise_project.id
  cidr_block = var.allowed_cidr_block
  comment    = "Acceso permitido para API local y despliegue en la nube"
}
