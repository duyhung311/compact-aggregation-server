public class MockRequests {
    public static final String VALID_PUT_REQUEST = """
            PUT /weather.json HTTP/1.1
            User-Agent: ATOMClient/1/0
            Content-Type: application/json
            Content-Length: 325
            Lamport-Clock: 0
            
            {"id":"IDS60901","name":"Adelaide (West Terrace /  ngayirdapira)","state":"SA","time_zone":"CST","lat":"-34.9","lon":"138.6","local_date_time_full":"20230715160000","air_temp":"13.3","apparent_t":"9.5","cloud":"Partly cloudy","dewpt":"5.7","press":"1023.9","rel_hum":"60","wind_dir":"S","wind_spd_kmh":"15","wind_spd_kt":"8"}
            """;

    public static final String NO_LAMPORT_CLOCK_REQUEST = """
            PUT /weather.json HTTP/1.1
            User-Agent: ATOMClient/1/0
            Content-Type: application/json
            Content-Length: 325
            
            {"id":"IDS60901","name":"Adelaide (West Terrace /  ngayirdapira)","state":"SA","time_zone":"CST","lat":"-34.9","lon":"138.6","local_date_time_full":"20230715160000","air_temp":"13.3","apparent_t":"9.5","cloud":"Partly cloudy","dewpt":"5.7","press":"1023.9","rel_hum":"60","wind_dir":"S","wind_spd_kmh":"15","wind_spd_kt":"8"}
            """;

    public static final String BROKE_REQUEST = """
            PUt /weather.json HTTP/1.1
            User-Agent: abcxyz
            ContEEnt-type: application/json
            ContEEnt-Length: 325
            Lamport: 0
            
            {"id":"IDS60901","name":"Adelaide (West Terrace /  ngayirdapira)","state":"SA","time_zone":"CST","lat":"-34.9","lon":"138.6","local_date_time_full":"20230715160000","air_temp":"13.3","apparent_t":"9.5","cloud":"Partly cloudy","dewpt":"5.7","press":"1023.9","rel_hum":"60","wind_dir":"S","wind_spd_kmh":"15","wind_spd_kt":"8"}
            """;

    public static final String GET_REQUEST = """
            GET /weather.json HTTP/1.1
            User-Agent: ATOMClient/1/0
            Lamport-Clock: 3
            """;
}
