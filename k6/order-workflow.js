import http from 'k6/http';
import { check, sleep } from 'k6';

// --- НАСТРОЙКИ ---
const ORDER_URL = __ENV.ORDER_URL || 'http://localhost:8081';
const AUTH_URL = __ENV.AUTH_URL || 'http://localhost:8080';
const TEST_ID = __ENV.TEST_ID || 'order-baseline-2';

// Данные для логина
const LOGIN_PAYLOAD = JSON.stringify({
    email: 'test1@example.com',
    password: '12345'
});

const ORDER_PAYLOAD = JSON.stringify({
    customerEmail: 'test1@example.com',
    items: [{ productName: 'Laptop', quantity: 2 }]
});

// --- ОПЦИИ ТЕСТА ---
export const options = {
    scenarios: {
        create_orders: {
            executor: 'ramping-arrival-rate',
            // Начинаем с 10 итераций в секунду
            startRate: 10,
            timeUnit: '1s',
            // Предварительно выделяем VUs (виртуальных пользователей)
            preAllocatedVUs: 50,
            // Максимальное количество VUs, которое k6 может создать при необходимости
            maxVUs: 200,
            stages: [
                { target: 10, duration: '30s' },  // Разогрев: держим 10 RPS 30 сек
                { target: 50, duration: '1m' },   // Плавно поднимаем до 50 RPS за 1 мин
                { target: 50, duration: '1m' },   // Держим 50 RPS 1 мин
                { target: 100, duration: '1m' },  // Плавно поднимаем до 100 RPS за 1 мин
                { target: 100, duration: '1m' },  // Держим 100 RPS 1 мин
                { target: 150, duration: '30s' }, // Резкий скачок до 150 RPS
                { target: 150, duration: '1m' },  // Держим 150 RPS 1 мин
                { target: 0, duration: '30s' },   // Плавное завершение
            ],
        },
    },
    thresholds: {
        // Порог по ошибкам: не более 5% (в стрессе ошибки допустимы, если мы ищем предел)
        http_req_failed: ['rate<0.05'],
        // Порог по времени: если p95 больше 1 секунды, значит сервис "поплыл"
        http_req_duration: ['p(95)<1000'],
    },
    tags: {
        testid: __ENV.TEST_ID || 'order-stress-1',
    },
};

// --- SETUP: ВЫПОЛНЯЕТСЯ 1 РАЗ ПЕРЕД ТЕСТОМ ---
export function setup() {
    console.log(`[Setup] Логинимся в ${AUTH_URL}/auth/login...`);

    const res = http.post(`${AUTH_URL}/api/auth/login`, LOGIN_PAYLOAD, {
        headers: { 'Content-Type': 'application/json' },
    });

    if (res.status !== 200) {
        throw new Error(`Не удалось залогиниться! Статус: ${res.status}, Тело: ${res.body}`);
    }

    // ВАЖНО: замените 'accessToken' на то поле, которое возвращает ваш user-service
    // Например, это может быть 'token', 'access_token' или 'jwt'
    const token = res.json('token');

    if (!token) {
        throw new Error(`Токен не найден в ответе. Ответ: ${res.body}`);
    }

    console.log('[Setup] Токен успешно получен.');
    return { token: token };
}

// --- DEFAULT: ВЫПОЛНЯЕТСЯ КАЖДЫМ VU ---
export default function (data) {
    // data.token берется из того, что вернул setup()
    const res = http.post(`${ORDER_URL}/api/orders`, ORDER_PAYLOAD, {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${data.token}`,
        },
        tags: { name: 'POST /orders' },
    });

    check(res, {
        'status is 200/201': (r) => r.status === 200 || r.status === 201,
    });

    sleep(0.1);
}