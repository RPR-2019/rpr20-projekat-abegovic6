BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS "notes" (
	"id"	INTEGER,
	"groupId"	INTEGER,
	"noteTitle"	TEXT,
	"description"	TEXT,
	"dateCreated"	TEXT,
	"dateUpdated"	TEXT,
	"noteColor"	TEXT,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "label" (
	"id"	INTEGER,
	"accountId"	INTEGER,
	"labelName"	TEXT,
	"description"	TEXT,
	"labelColor"	TEXT,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "group" (
	"id"	INTEGER,
	"accountId"	INTEGER,
	"groupName"	TEXT,
	"description"	TEXT,
	"groupColor"	TEXT,
	PRIMARY KEY("id")
);
CREATE TABLE IF NOT EXISTS "account" (
	"id"	INTEGER,
	"firstName"	TEXT,
	"lastName"	TEXT,
	"userName"	TEXT,
	"emailAdress"	TEXT,
	"password"	TEXT,
	PRIMARY KEY("id")
);
COMMIT;
