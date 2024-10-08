#!/bin/bash
# run from inside maintenance directory, unless "../db_backups/" will not work

# Define variables
DB_USER="easybudgetadmin"
DB_PASSWORD="easybudgetpw"
DB_NAME="easybudgetdb"
BACKUP_DIR="../db_backups"
TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
BACKUP_FILE="$BACKUP_DIR/backup_$TIMESTAMP.sql"

# Run the mysqldump command
mysqldump -u $DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_FILE

# Check if the command succeeded
if [ $? -eq 0 ]; then
    echo "Backup completed successfully: $BACKUP_FILE"
else
    echo "Backup failed."
    exit 1
fi
