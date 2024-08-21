CREATE TABLE rentals (
    id INT PRIMARY KEY,
    rental_date DATE,
    return_date DATE,
    actual_return_date DATE,
    car_id INT,
    user_id INT
);
