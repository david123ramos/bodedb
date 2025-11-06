import SaveURLs from "./Save-URLs.js";
import { group, sleep } from "k6";


export default () => {
    group('Endpoint save-url - Controller URL - ', () => {
        SaveURLs();
    });

    //sleep(1);
};
