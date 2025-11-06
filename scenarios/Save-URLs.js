import http from 'k6/http';
import { randomSeed, sleep } from 'k6';
import {Rate, Trend} from 'k6/metrics';
import { check, fail } from 'k6';
import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export let SaveURLFailRate = new Rate('save_url_fail_rate');

const BASE_URL = 'http://localhost:8080/v1';

export default function() {

    const params = {
        headers: { 'Content-Type': 'application/json' },
    };
    
    const resp = http.post(BASE_URL+"/url",  JSON.stringify({
            "url": "www.yt.com/"+randomString(10),
            "count" : Math.round(Math.random() * 100)
    }), params);

    SaveURLFailRate.add(resp.status == 0 || resp.status > 399);
}