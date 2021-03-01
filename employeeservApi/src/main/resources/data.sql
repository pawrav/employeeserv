INSERT INTO employee (first_name, last_name, date_of_birth) VALUES
  ('First1', 'Last1', '2000-11-21');

INSERT INTO address (employee_id, line1, city, state, country, zip_code) VALUES
(select id from employee where first_name = 'First1', 'Line1', 'City1', 'State1', 'Country1', 'ZipCode1');