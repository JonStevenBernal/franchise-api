output "atlas_project_id" {
  description = "ID del proyecto creado en MongoDB Atlas"
  value       = mongodbatlas_project.franchise_project.id
}

output "cluster_name" {
  description = "Nombre del clúster M0 aprovisionado en AWS"
  value       = mongodbatlas_advanced_cluster.franchise_cluster.name
}

output "mongodb_srv_host" {
  description = "URL base SRV del clúster (sin credenciales)"
  value       = mongodbatlas_advanced_cluster.franchise_cluster.connection_strings.standard_srv
}

output "mongodb_connection_uri" {
  description = "URI completa lista para SPRING_MONGODB_URI"
  value       = "${replace(mongodbatlas_advanced_cluster.franchise_cluster.connection_strings.standard_srv, "mongodb+srv://", "mongodb+srv://${urlencode(var.db_username)}:${urlencode(var.db_password)}@")}/${var.db_name}?retryWrites=true&w=majority&appName=${var.cluster_name}"
  sensitive   = true
}
