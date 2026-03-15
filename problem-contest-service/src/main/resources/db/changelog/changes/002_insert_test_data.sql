-- ====== ЗАДАЧИ =======

-- публичная задача 1
INSERT INTO problems (id, creator_id, is_public, title, statement, input, output,
                      sample_count, sample_input, sample_output, complexity,
                      compile_time_limit, time_limit, memory_limit)
VALUES
    (1, 1, TRUE, 'A + B',
     'Даны два целых числа A и B. Выведите их сумму.

    Входные данные:
    Два целых числа A и B (-10^9 ≤ A, B ≤ 10^9), разделённых пробелом.

    Выходные данные:
    Одно целое число — сумма A + B.',
     'Два целых числа в одной строке',
     'Одно целое число — сумма',
     3,
     ARRAY['2 3', '-5 10', '1000000000 1000000000'],
     ARRAY['5', '5', '2000000000'],
     1, 5.0, 1.0, 64.0);

-- публичная задача 2
INSERT INTO problems (id, creator_id, is_public, title, statement, input, output,
                      sample_count, sample_input, sample_output, complexity,
                      compile_time_limit, time_limit, memory_limit)
VALUES
    (2, 1, TRUE, 'Сортировка',
     'Дан массив из N целых чисел. Отсортируйте его по возрастанию.

    Входные данные:
    Первая строка: число N (1 ≤ N ≤ 10^5)
    Вторая строка: N целых чисел (-10^9 ≤ Ai ≤ 10^9)

    Выходные данные:
    Отсортированный массив в одну строку через пробел.',
     'N и массив чисел',
     'Отсортированный массив',
     2,
     ARRAY['5\n3 1 4 1 5', '3\n-10 0 10'],
     ARRAY['1 1 3 4 5', '-10 0 10'],
     5, 5.0, 2.0, 256.0);


-- приватная задача 1
INSERT INTO problems (id, creator_id, is_public, title, statement, input, output,
                      sample_count, sample_input, sample_output, complexity,
                      compile_time_limit, time_limit, memory_limit)
VALUES
    (3, 1, FALSE, 'Кратчайший путь',
     'Дан неориентированный граф. Найдите кратчайшее расстояние от вершины S до вершины F.

    Входные данные:
    Первая строка: N, M, S, F (число вершин, рёбер, старт, финиш)
    Следующие M строк: пары вершин, соединённых ребром

    Выходные данные:
    Длина кратчайшего пути или -1, если путь не существует.',
     'Описание графа',
     'Длина пути или -1',
     2,
     ARRAY['4 4 1 4\n1 2\n1 3\n2 4\n3 4', '3 1 1 3\n1 2'],
     ARRAY['2', '-1'],
     7, 5.0, 2.0, 256.0);





-- ========== КОНТЕСТЫ ==========

-- Контест 1. Активный контест
INSERT INTO contests (id, title, author_id, start_time, end_time)
VALUES
    (1, 'Junior Contest', 1,
     CURRENT_TIMESTAMP - INTERVAL '30 minutes',
     CURRENT_TIMESTAMP + INTERVAL '100 hour 30 minutes');


-- Задачи контеста
INSERT INTO contest_problem (contest_id, problem_id, score)
VALUES
    (1, 1, 100),
    (1, 2, 200),
    (1, 3, 300);


-- Участники контеста

INSERT INTO contest_user (contest_id, user_id, total_score)
VALUES
    (1, 1, 0),
    (1, 2, 300);


-- ========== РЕЗУЛЬТАТЫ ПО ЗАДАЧАМ ==========


INSERT INTO contest_user_task (contest_id, user_id, task_id, solved, attempts, score, solution_time)
VALUES
    (1, 1, 1, FALSE, 5, 0, NULL),
    (1, 2, 1, TRUE, 3, 100, CURRENT_TIMESTAMP - INTERVAL '20 minutes'),
    (1, 2, 2, TRUE, 1, 200, CURRENT_TIMESTAMP - INTERVAL '10 minutes');

