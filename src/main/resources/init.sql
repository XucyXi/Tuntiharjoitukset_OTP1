CREATE TABLE IF NOT EXISTS temperature_unit (
                                                id INT PRIMARY KEY,
                                                symbol VARCHAR(5) NOT NULL UNIQUE
    );

CREATE TABLE IF NOT EXISTS unit_translation (
                                                unit_id INT,
                                                lang VARCHAR(5),
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100),
    PRIMARY KEY (unit_id, lang),
    FOREIGN KEY (unit_id) REFERENCES temperature_unit(id)
    );

CREATE TABLE IF NOT EXISTS temp_record (
                                           id INT AUTO_INCREMENT PRIMARY KEY,
                                           from_unit_id INT NOT NULL,
                                           to_unit_id INT NOT NULL,
                                           input_value DOUBLE NOT NULL,
                                           result_value DOUBLE NOT NULL,
                                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           FOREIGN KEY (from_unit_id) REFERENCES temperature_unit(id),
    FOREIGN KEY (to_unit_id) REFERENCES temperature_unit(id)
    );

-- Perusyksiköt
MERGE INTO temperature_unit (id, symbol) KEY(id) VALUES (1, '°C');
MERGE INTO temperature_unit (id, symbol) KEY(id) VALUES (2, '°F');
MERGE INTO temperature_unit (id, symbol) KEY(id) VALUES (3, 'K');

-- Käännökset (Englanti, Suomi, Ruotsi, Japani, Arabia)
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (1, 'en', 'Celsius', 'Degrees Celsius');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (1, 'fi', 'Celsius', 'Celsiusaste');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (1, 'sv', 'Celsius', 'Grader Celsius');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (1, 'ja', 'セルシウス度', '摂氏');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (1, 'ar', 'درجة مئوية', 'درجة مئوية');

MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (2, 'en', 'Fahrenheit', 'Degrees Fahrenheit');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (2, 'fi', 'Fahrenheit', 'Fahrenheitaste');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (2, 'sv', 'Fahrenheit', 'Grader Fahrenheit');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (2, 'ja', '華氏', '華氏');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (2, 'ar', 'فهرنهايت', 'فهرنهايت');

MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (3, 'en', 'Kelvin', 'Kelvin scale');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (3, 'fi', 'Kelvin', 'Kelvin-asteikko');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (3, 'sv', 'Kelvin', 'Kelvinskalan');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (3, 'ja', 'ケルビン', 'ケルビン');
MERGE INTO unit_translation (unit_id, lang, name, description) KEY(unit_id, lang) VALUES (3, 'ar', 'كلفن', 'كلفن');