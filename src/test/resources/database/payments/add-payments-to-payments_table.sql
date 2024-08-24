INSERT INTO payments (id, status, type, rental_id, session_url, session_id, amount_to_pay) VALUES
(1, 'PENDING', 'PAYMENT', 1, 'http://example.com/session1', 'session1', 100.00),
(2, 'PAID', 'FINE', 1, 'http://example.com/session2', 'session2', 200.00),
(3, 'PENDING', 'PAYMENT', 2, 'http://example.com/session3', 'session3', 300.00);
