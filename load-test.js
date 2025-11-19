import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 10 },   // Разгон до 10 пользователей
    { duration: '1m', target: 10 },    // Держим 10 пользователей
    { duration: '30s', target: 50 },   // Разгон до 50 пользователей
    { duration: '2m', target: 50 },    // Держим 50 пользователей
    { duration: '30s', target: 0 },    // Снижаем до 0
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],  // 95% запросов < 500ms
    http_req_failed: ['rate<0.01'],    // Меньше 1% ошибок
  },
};

export default function () {
  // Тест сброса пароля
  const resetPayload = JSON.stringify({
    email: 'anika.torphy@yahoo.com'
  });

  const resetRes = http.post(
    'https://api.dev.tutorplace.ru/reset',
    resetPayload,
    { headers: { 'Content-Type': 'application/json' } }
  );

  check(resetRes, {
    'reset status is 200': (r) => r.status === 200,
    'reset response time OK': (r) => r.timings.duration < 1000,
  });

  sleep(1);

  // Тест логина
  const loginPayload = JSON.stringify({
    email: 'tp5052_4@test.ru',
    password: '12345678'
  });

  const loginRes = http.post(
    'https://api.dev.tutorplace.ru/user/login',
    loginPayload,
    { headers: { 'Content-Type': 'application/json' } }
  );

  check(loginRes, {
    'login status is 200': (r) => r.status === 200,
    'login response time OK': (r) => r.timings.duration < 500,
  });

  sleep(1);
}