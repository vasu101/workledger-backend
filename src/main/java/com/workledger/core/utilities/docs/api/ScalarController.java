package com.workledger.core.utilities.docs.api;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ScalarController {

    @GetMapping("/scalar")
    public void scalar(HttpServletResponse response) throws Exception {
        String html = """
            <!doctype html>
            <html>
            <head><title>WorkLedger API</title>
              <meta charset="utf-8"/>
              <meta name="viewport" content="width=device-width, initial-scale=1"/>
            </head>
            <body>
              <script
                id="api-reference"
                data-url="/api-docs">
              </script>
              <script src="https://cdn.jsdelivr.net/npm/@scalar/api-reference"></script>
            </body>
            </html>
            """;
        response.setContentType("text/html");
        response.getWriter().write(html);
    }
}