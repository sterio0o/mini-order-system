import http from 'k6/http';
import { check, sleep } from 'k6';

// --- НАСТРОЙКИ ---
const ORDER_URL = __ENV.ORDER_URL || 'http://localhost:8081';
const AUTH_URL = __ENV.AUTH_URL || 'http://localhost:8080';
const TEST_ID = __ENV.TEST_ID || 'multi-user-stress';

// Список тестовых пользователей (созданных вами в БД)
// Пароль у всех одинаковый: 12345
const USERS = [
    { email: 'user1@example.com' },
    { email: 'user2@example.com' },
    { email: 'user3@example.com' },
    { email: 'user4@example.com' },
    { email: 'user5@example.com' },
    { email: 'user6@example.com' },
    { email: 'user7@example.com' },
    { email: 'user8@example.com' },
    { email: 'user9@example.com' },
    { email: 'user10@example.com' },
    { email: 'user11@example.com' },
    { email: 'user12@example.com' },
];

const ORDER_ITEMS = [{ productName: 'Laptop', quantity: 1 }];

// --- ОПЦИИ ТЕСТА ---
export const options = {
    scenarios: {
        create_orders: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            maxVUs: 200,
            stages: [
                { target: 10, duration: '30s' },
                { target: 50, duration: '1m' },
                { target: 100, duration: '1m' },
                { target: 150, duration: '1m' },
                { target: 150, duration: '1m' },
                { target: 0, duration: '30s' },
            ],
        },
    },
    thresholds: {
        http_req_failed: ['rate<0.05'],
        http_req_duration: ['p(95)<1000'],
    },
    tags: {
        testid: TEST_ID,
    },
};

// --- SETUP: ЛОГИНИМ ВСЕХ ПОЛЬЗОВАТЕЛЕЙ ---
export function setup() {
    console.log(`[Setup] Логинимся под ${USERS.length} пользователями...`);
    const authenticatedUsers = [];

    for (const user of USERS) {
        const loginPayload = JSON.stringify({
            email: user.email,
            password: '12345' // Пароль у всех одинаковый
        });

        const res = http.post(`${AUTH_URL}/api/auth/login`, loginPayload, {
            headers: { 'Content-Type': 'application/json' },
        });

        if (res.status !== 200) {
            console.warn(`[Setup] Не удалось залогинить ${user.email}. Статус: ${res.status}`);
            continue; // Пропускаем, если не удалось
        }

        const token = res.json('token');
        if (token) {
            authenticatedUsers.push({ email: user.email, token: token });
        }
    }

    if (authenticatedUsers.length === 0) {
        throw new Error('Ни один пользователь не смог залогиниться. Проверьте данные.');
    }

    console.log(`[Setup] Успешно залогинено ${authenticatedUsers.length} пользователей.`);
    return { users: authenticatedUsers };
}

// --- DEFAULT: ВЫПОЛНЯЕТСЯ КАЖДЫМ VU ---
export default function (data) {
    // 1. Выбираем СЛУЧАЙНОГО пользователя из списка
    const randomUser = data.users[Math.floor(Math.random() * data.users.length)];

    // 2. Формируем payload с email этого пользователя
    const orderPayload = JSON.stringify({
        customerEmail: randomUser.email,
        items: ORDER_ITEMS
    });

    // 3. Отправляем запрос с его токеном
    const res = http.post(`${ORDER_URL}/api/orders`, orderPayload, {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${randomUser.token}`,
        },
        tags: { name: 'POST /orders' },
    });

    check(res, {
        'status is 200/201': (r) => r.status === 200 || r.status === 201,
    });

    // Имитация реального пользователя (пауза 0.5 - 2 сек)
    sleep(Math.random() * 1.5 + 0.5);
}