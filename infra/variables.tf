variable "project_id" {
  description = "pay-stream"
  type        = string
}

variable "region" {
  description = "GCP region"
  type        = string
  default     = "us-central1"
}

variable "db_password" {
  description = "Unleashed.B3ast42*"
  type        = string
  sensitive   = true
}