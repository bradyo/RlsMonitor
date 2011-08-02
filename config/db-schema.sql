
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT, 
    username VARCHAR(128) NOT NULL UNIQUE, 
    email VARCHAR(128) NOT NULL,
    algorithm VARCHAR(128) DEFAULT 'sha1' NOT NULL, 
    salt VARCHAR(128), 
    password VARCHAR(128), 
    lab VARCHAR(128), 
    location VARCHAR(128), 
    phone VARCHAR(32), 
    created_at DATETIME NOT NULL, 
    updated_at DATETIME NOT NULL, 
    INDEX username_idx (username)
) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS yeast_strain (
    id INT PRIMARY KEY AUTO_INCREMENT, 
    name VARCHAR(128) NOT NULL UNIQUE, 
    owner VARCHAR(128), 
    background VARCHAR(255), 
    mating_type VARCHAR(255), 
    genotype VARCHAR(255), 
    genotype_short VARCHAR(255),
    genotype_unique VARCHAR(255), 
    freezer_code VARCHAR(255), 
    comment TEXT, 
    is_locked VARCHAR(1), 
    created_at DATETIME NOT NULL, 
    updated_at DATETIME NOT NULL, 
    INDEX owner_idx (owner),
    FOREIGN KEY (owner) REFERENCES `user` (username) ON DELETE CASCADE
) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS yeast_rls_experiment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    facility VARCHAR(64),
    number INT NULL,
    name VARCHAR(64),
    description TEXT NULL,
    key_data TEXT NOT NULL,
    requested_by VARCHAR(64) NOT NULL,
    requested_at TIMESTAMP,
    request_message TEXT NULL,
    reviewed_at TIMESTAMP NULL,
    status VARCHAR(64) NULL,
    review_message TEXT NULL,
    completed_at TIMESTAMP
) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE = INNODB;

CREATE TABLE IF NOT EXISTS yeast_rls_cell_set (
    id INT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    experiment_id INT NOT NULL,
    number INT NOT NULL,
    reference VARCHAR(64),
    label VARCHAR(255),
    strain_name VARCHAR(64),
    strain_id INT,
    media VARCHAR(255),
    temperature DECIMAL(5,2),
    cell_count INT,
    INDEX(number), INDEX(label), INDEX(strain_name), INDEX(media), INDEX(temperature), 
    INDEX(cell_count),
    FOREIGN KEY (experiment_id) REFERENCES yeast_rls_experiment (id) ON DELETE CASCADE,
    FOREIGN KEY (strain_id) REFERENCES yeast_strain (id) ON DELETE SET NULL
) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE = INNODB;


CREATE TABLE IF NOT EXISTS yeast_rls_update_queue (
    experiment_id INT NOT NULL,
    created_at TIMESTAMP,
    INDEX (created_at),
    FOREIGN KEY (experiment_id) REFERENCES yeast_rls_experiment(id) ON DELETE CASCADE
) DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ENGINE = INNODB;

