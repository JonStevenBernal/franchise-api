variable "atlas_org_id" {
  type        = string
  description = "ID de la organización en MongoDB Atlas"
}

variable "atlas_client_id" {
  type        = string
  default     = null
  sensitive   = true
  description = "Client ID del Service Account en MongoDB Atlas (opcional si usas API Key)"
}

variable "atlas_client_secret" {
  type        = string
  default     = null
  sensitive   = true
  description = "Client Secret del Service Account en MongoDB Atlas (opcional si usas API Key)"
}

variable "atlas_public_key" {
  type        = string
  default     = null
  sensitive   = true
  description = "Public Key de la API Key en MongoDB Atlas (opcional si usas Service Account)"
}

variable "atlas_private_key" {
  type        = string
  default     = null
  sensitive   = true
  description = "Private Key de la API Key en MongoDB Atlas (opcional si usas Service Account)"
}

variable "project_name" {
  type        = string
  default     = "franchise-api-iac"
  description = "Nombre del proyecto a crear en MongoDB Atlas"
}

variable "cluster_name" {
  type        = string
  default     = "franchise-aws-cluster"
  description = "Nombre del clúster M0 en MongoDB Atlas"
}

variable "aws_region" {
  type        = string
  default     = "US_EAST_1"
  description = "Región de AWS donde se aprovisionará el clúster MongoDB Atlas"
}

variable "db_name" {
  type        = string
  default     = "franchise_db"
  description = "Nombre de la base de datos usada por la API"
}

variable "db_username" {
  type        = string
  default     = "franchise_user"
  description = "Usuario de base de datos con acceso a la aplicación"
}

variable "db_password" {
  type        = string
  sensitive   = true
  description = "Contraseña del usuario de base de datos"
}

variable "allowed_cidr_block" {
  type        = string
  default     = "0.0.0.0/0"
  description = "Bloque CIDR permitido para conectarse al clúster en Atlas"
}

