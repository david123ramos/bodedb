import http from 'k6/http';
import {Rate, Trend} from 'k6/metrics';
//import { randomString } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

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

function randomString(size) {
    const alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; //62
    let result = "";
    for(let i = 0; i <= size; ++i) {
        const random = (Math.round(Math.random() * 100) % alphabet.length) ;
        result += alphabet.at(random);
    }
    return result;
}