CREATE TABLE companies (
    company_id VARCHAR(36) PRIMARY KEY,
    employer_id VARCHAR(36) NOT NULL,
    company_name VARCHAR(255) NOT NULL,
    address TEXT,
    contact_phone VARCHAR(20),
    business_license VARCHAR(100),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL,
    FOREIGN KEY (employer_id) REFERENCES users(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_employer_id ON companies(employer_id);