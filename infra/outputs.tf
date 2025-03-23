output "service_url" {
  value = google_cloud_run_service.transaction_service.status[0].url
}

output "db_connection_name" {
  value = google_sql_database_instance.postgres.connection_name
}