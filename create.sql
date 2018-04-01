CREATE TABLE IF NOT EXISTS users (
  user_id int NOT NULL AUTO_INCREMENT,
  user_name VARCHAR(50),
  password VARCHAR(50),
  email VARCHAR(50),
  PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS messages(
  msg_id int NOT NULL AUTO_INCREMENT,
  sender int NOT NULL ,
  receiver int NOT NULL ,
  messageContent VARCHAR(200),
  PRIMARY KEY(msg_id),
  FOREIGN KEY(sender) REFERENCES users(user_id),
  FOREIGN KEY(receiver) REFERENCES users(user_id)
);