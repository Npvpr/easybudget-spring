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
sudo mysqldump -u $DB_USER -p$DB_PASSWORD $DB_NAME > $BACKUP_FILE

# Check if the command succeeded
if [ $? -eq 0 ]; then
    echo "Backup completed successfully: $BACKUP_FILE"
else
    echo "Backup failed."
    exit 1
fi

# mysqldump: Error: 'Access denied; you need (at least one of) the PROCESS privilege(s) for this operation' when trying to dump tablespaces
# grant process access to this admin to all databases
# GRANT PROCESS ON *.* TO 'easybudgetadmin'@'localhost';
# FLUSH PRIVILEGES;


# Restore database from a .sql file
# sudo mysql -u easybudgetadmin -peasybudgetpw easybudgetdb < /path/to/your/file.sql
