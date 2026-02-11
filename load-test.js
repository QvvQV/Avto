import http from 'k6/http';
import { htmlReport } from "https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js";
import { textSummary } from "https://jslib.k6.io/k6-utils/1.4.0/index.js";
import { check, sleep, group } from 'k6';

export const options = {
  vus: 5,
  duration: '1m',
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  group('API Test Suite', function () {
    // Test 1
    const response1 = http.get('https://httpbin.test.k6.io/get');
    check(response1, {
      'status is 200': (r) => r.status === 200,
      'response has headers': (r) => r.headers['Content-Type'] !== null,
    });

    // Test 2
    const response2 = http.post('https://httpbin.test.k6.io/post', {
      test: 'data'
    });
    check(response2, {
      'post status is 200': (r) => r.status === 200,
      'post response ok': (r) => r.json().json.test === 'data',
    });
  });
}

export function handleSummary(data) {
  // Сохраняем детальные результаты
  const detailedResults = {
    timestamp: new Date().toISOString(),
    metrics: data.metrics,
    options: data.options
  };

  // Используем файловую систему k6
  if (typeof __ENV.K6_ALLURE_RESULTS !== 'undefined') {
    const fs = require('k6/x/file');
    const resultsDir = __ENV.K6_ALLURE_RESULTS;
    if (!fs.existsSync(resultsDir)) {
      fs.mkdirSync(resultsDir, { recursive: true });
    }
    fs.writeStringSync(`${resultsDir}/k6-data.json`, JSON.stringify(detailedResults));
  }

  return {
    'stdout': textSummary(data, { indent: ' ', enableColors: true }),
  };
}

//export const options = {
//  stages: [
//    { duration: '30s', target: 10 },   // Разгон до 10 пользователей
//    { duration: '1m', target: 10 },    // Держим 10 пользователей
//    { duration: '30s', target: 50 },   // Разгон до 50 пользователей
//    { duration: '2m', target: 50 },    // Держим 50 пользователей
//    { duration: '30s', target: 0 },    // Снижаем до 0
//  ],
//  thresholds: {
//    http_req_duration: ['p(95)<500'],  // 95% запросов < 500ms
//    http_req_failed: ['rate<0.01'],    // Меньше 1% ошибок
//  },
//};
//
//export default function () {
//  // Тест сброса пароля
//  const resetPayload = JSON.stringify({
//    email: 'anika.torphy@yahoo.com'
//  });
//
//  const resetRes = http.post(
//    'https://api.dev.tutorplace.ru/reset',
//    resetPayload,
//    { headers: { 'Content-Type': 'application/json' } }
//  );
//
//  check(resetRes, {
//    'reset status is 200': (r) => r.status === 200,
//    'reset response time OK': (r) => r.timings.duration < 1000,
//  });
//
//  sleep(1);
//
//  // Тест логина
//  const loginPayload = JSON.stringify({
//    email: 'tp5052_4@test.ru',
//    password: '12345678'
//  });
//
//  const loginRes = http.post(
//    'https://api.dev.tutorplace.ru/user/login',
//    loginPayload,
//    { headers: { 'Content-Type': 'application/json' } }
//  );
//
//  check(loginRes, {
//    'login status is 200': (r) => r.status === 200,
//    'login response time OK': (r) => r.timings.duration < 500,
//  });
//
//  sleep(1);
//}