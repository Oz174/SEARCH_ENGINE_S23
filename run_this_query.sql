/***
This set of queries does the following:
1. Clears the database
2. Alters "Docs" table to add a new column "visit_count"
***/

-- Clear the database
DELETE FROM Docs;
DBCC CHECKIDENT ('Docs', RESEED, 0);

-- Add a new column
ALTER TABLE Docs
ADD visit_count INT NOT NULL DEFAULT 1;