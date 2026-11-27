import http from 'k6/http';
import { check, sleep } from 'k6';

// --- НАСТРОЙКИ ---
const ORDER_URL = __ENV.ORDER_URL || 'http://localhost:8081';
const AUTH_URL = __ENV.AUTH_URL || 'http://localhost:8080';
const TEST_ID = __ENV.TEST_ID || 'cache-stress-test';

const USERS = [
    { email: 'user1@example.com' }, { email: 'user2@example.com' },
    { email: 'user3@example.com' }, { email: 'user4@example.com' },
    { email: 'user5@example.com' }, { email: 'user6@example.com' },
    { email: 'user7@example.com' }, { email: 'user8@example.com' },
    { email: 'user9@example.com' }, { email: 'user10@example.com' },
    { email: 'user11@example.com' }, { email: 'user12@example.com' },
];

/*
    Laptop: a50ea34e-ad86-4151-abed-9e2ce640b2c0
    Mouse: 775ca341-4ef8-4e0d-9593-e0e6039be24c
    Keyboard: c472bdcd-8c69-44a8-8989-22ee7c86d113
    Monitor: 1c9dfa73-7fef-4b40-a739-fad48d239a51
    Headphones: 8c5a8b01-59db-48cc-af1c-08233f013ac5
 */
// Список товаров (точные названия из вашей БД)
// Чтобы симулировать разную популярность, некоторые товары встречаются чаще
const PRODUCTS = [
    'a50ea34e-ad86-4151-abed-9e2ce640b2c0', 'a50ea34e-ad86-4151-abed-9e2ce640b2c0', 'a50ea34e-ad86-4151-abed-9e2ce640b2c0', 'a50ea34e-ad86-4151-abed-9e2ce640b2c0', // Хит продаж
    '775ca341-4ef8-4e0d-9593-e0e6039be24c', '775ca341-4ef8-4e0d-9593-e0e6039be24c', '775ca341-4ef8-4e0d-9593-e0e6039be24c',
    'c472bdcd-8c69-44a8-8989-22ee7c86d113', 'c472bdcd-8c69-44a8-8989-22ee7c86d113', 'c472bdcd-8c69-44a8-8989-22ee7c86d113',
    '1c9dfa73-7fef-4b40-a739-fad48d239a51', '1c9dfa73-7fef-4b40-a739-fad48d239a51',
    '8c5a8b01-59db-48cc-af1c-08233f013ac5' // Редкий товар
];

// --- ОПЦИИ ТЕСТА ---
export const options = {
    scenarios: {
        create_orders: {
            executor: 'ramping-arrival-rate',
            startRate: 10,
            timeUnit: '1s',
            preAllocatedVUs: 50,
            maxVUs: 300, // Увеличил, чтобы избежать dropped_iterations
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

export function setup() {
    console.log(`[Setup] Логинимся...`);
    const authenticatedUsers = [];
    for (const user of USERS) {
        const res = http.post(`${AUTH_URL}/api/auth/login`, JSON.stringify({
            email: user.email, password: '12345'
        }), { headers: { 'Content-Type': 'application/json' } });

        if (res.status === 200) {
            const token = res.json('token');
            if (token) authenticatedUsers.push({ email: user.email, token: token });
        }
    }
    return { users: authenticatedUsers };
}

export default function (data) {
    // Выбираем случайного юзера
    const randomUser = data.users[Math.floor(Math.random() * data.users.length)];

    // Выбираем случайный товар из списка
    const randomProduct = PRODUCTS[Math.floor(Math.random() * PRODUCTS.length)];

    // Рандомное количество (1-3 шт)
    const quantity = Math.floor(Math.random() * 3) + 1;

    const orderPayload = JSON.stringify({
        customerEmail: randomUser.email,
        items: [{ productId: randomProduct, quantity: quantity }]
    });

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

    sleep(Math.random() * 1.5 + 0.5);
}