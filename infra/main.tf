# Configure the GCP provider
provider "google" {
  project = var.project_id
  region  = var.region
}

# Create a Cloud SQL PostgreSQL instance
resource "google_sql_database_instance" "postgres" {
  name             = "pay-stream-db"
  database_version = "POSTGRES_15"
  region           = var.region

  settings {
    tier = "db-f1-micro"
    ip_configuration {
      ipv4_enabled = true  # Enable public IP for simplicity (disable in prod)
    }
  }
}

# Create a database
resource "google_sql_database" "transactions" {
  name     = "transactions"
  instance = google_sql_database_instance.postgres.name
}

# Create a database user
resource "google_sql_user" "quarkus" {
  name     = "quarkus"
  instance = google_sql_database_instance.postgres.name
  password = var.db_password
}

# Deploy Cloud Run service
resource "google_cloud_run_service" "transaction_service" {
  name     = "transaction-service"
  location = var.region

  template {
    spec {
      containers {
        # Use the image you pushed to Artifact Registry
        image = "us-central1-docker.pkg.dev/${var.project_id}/pay-stream-repo/transaction-service:latest"

        # Environment variables for database connection
        env {
          name  = "QUARKUS_DATASOURCE_JDBC_URL"
          value = "jdbc:postgresql:///${google_sql_database.transactions.name}?cloudSqlInstance=${var.project_id}:${var.region}:${google_sql_database_instance.postgres.name}&socketFactory=com.google.cloud.sql.postgres.SocketFactory"
        }
        env {
          name  = "QUARKUS_DATASOURCE_USERNAME"
          value = google_sql_user.quarkus.name
        }
        env {
          name  = "QUARKUS_DATASOURCE_PASSWORD"
          value = var.db_password
        }

        startup_probe {
          initial_delay_seconds = 5
          timeout_seconds      = 10
          period_seconds       = 15
          failure_threshold    = 3
          tcp_socket {
            port = 8080
          }
        }
      }
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }
}

# Allow public access to Cloud Run
data "google_iam_policy" "noauth" {
  binding {
    role = "roles/run.invoker"
    members = ["allUsers"]
  }
}

resource "google_cloud_run_service_iam_policy" "noauth" {
  location = google_cloud_run_service.transaction_service.location
  project  = google_cloud_run_service.transaction_service.project
  service  = google_cloud_run_service.transaction_service.name
  policy_data = data.google_iam_policy.noauth.policy_data
}