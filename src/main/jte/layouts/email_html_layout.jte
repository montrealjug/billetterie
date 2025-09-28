@param String title
@param String h1
@param gg.jte.Content mainContent
@param gg.jte.Content additionalHeadContent = null
<!DOCTYPE html>
<html lang="fr-CA">
<head>
  <meta charset="UTF-8">
  <meta http-equiv="Content-Type" content="text/html charset=UTF-8"/>
  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
  <title>${title}</title>
  <style>
    body {
      padding: 0.2em 1em;
      font-family: 'Helvetica Neue', Helvetica, Arial, 'sans-serif';
      box-sizing: border-box;
      background-color: #777;
      mso-line-height-rule: exactly;
    }
    h1 {
      margin: 0;
    }
    table.layout {
      border: none;
      border-radius: 4px;
      background-color: white;
      width: 600px;
      margin: auto;
      border-collapse: collapse;
    }
    td.layout {
      padding: 1em;
      mso-line-height-rule: exactly;
    }
    span.italic-grey {
      font-style: italic;
      color: #777;
    }
    p.english-version {
      font-style: italic;
      color: #777;
      display: inline-block;
      margin: 2em 0 1em 0;
    }
    table.header {
      border-collapse: collapse;
      border-spacing: 0;
      border: none;
      border-radius: 0;
      margin: 0;
    }
    table.main-table {
      border-collapse: collapse;
      border-spacing: 0;
      border-radius: 0;
      border: none;
      margin: 0;
      line-height: 1.8em;
    }
    table.footer {
      border-collapse: collapse;
      border-spacing: 0;
      border: none;
      border-radius: 0;
      margin: 0;
      font-size: 0.8em;
      color: #777;
      width: 100%;
    }
    td.with-top-bottom-padding {
      padding: 0.8em 0;
    }
    td.italic {
      font-style: italic;
      padding: 0;
    }
    td.footer-left {
      font-style: italic;
    }
    td.footer-right {
      text-align: right;
    }
    a.link-button {
      font-size: 0.8em;
      background-color: #f0690a;
      text-decoration: none;
      padding: 0.2em 0.8em;
      color: white;
      display: inline-block;
      border-radius: 4px;
      mso-padding-alt: 0;
      text-underline-color: #f0690a;
      border: 2px solid #f0690a;
    }
    a.link-button:hover {
      background-color: white;
      color: #f0690a;
    }
  </style>
  @if (additionalHeadContent != null)
    ${additionalHeadContent}
  @endif
</head>
<body>
  <table class="layout">
    <tr>
      <td class="layout">
        <header>
          <table class="header">
            <tbody>
              <tr>
                <td>
                    <a href="https://www.devoxx4kids.org/quebec/" target="_blank" rel="noopener noreferrer">
                      <img src="https://www.devoxx4kids.org/quebec/wp-content/uploads/sites/12/2014/06/D4K_QUEBEC_1000px.png"
                           alt="Devoxx4Kids Québec logo"
                           width="560"
                           style="width: 560px; margin: auto;"
                      />
                    </a>
                </td>
              </tr>
              <tr>
                <td>
                  <span class="italic-grey">Devoxx4Kids Québec</span>
                </td>
              </tr>
              <tr>
                <td style="padding: 1.8em 0;">
                  <h1>${h1}</h1>
                </td>
              </tr>
              <tr>
                <td>
                  <span class="italic-grey">The English version will follow</span>
                </td>
              </tr>
            </tbody>
          </table>
        </header>
      </td>
    </tr>
    <tr>
      <td class="layout">
        <main>
          ${mainContent}
        </main>
      </td>
    </tr>
    <tr>
      <td class="layout">
        <footer>
          <table class="footer">
            <tbody>
              <tr>
                <td class="footer-left">
                  &copy; ${java.time.LocalDate.now().getYear()} Québec – All rights reserved
                </td>
                <td class="footer-right">
                  <table style="border: none; border-spacing: 0; border-collapse: collapse; margin: auto 0 auto auto; width: 100px;">
                    <tbody>
                      <tr>
                        <td>
                          <a href="https://www.facebook.com/Devoxx4KidsQC/">
                            <img src="https://www.devoxx4kids.org/quebec/wp-content/uploads/sites/12/2024/11/facebook-300x300.png"
                                 alt="Facebook logo"
                                 width="32"
                                 height="32"
                                 style="display: inline; width: 32px;"
                            />
                          </a>
                        </td>
                        <td>
                          <a href="https://www.linkedin.com/company/devoxx4kids-quebec/?viewAsMember=true">
                            <img src="https://www.devoxx4kids.org/quebec/wp-content/uploads/sites/12/2024/11/linkedin-300x300.png"
                                 alt="LinkedIn logo"
                                 width="32"
                                 height="32"
                                 style="display: inline; width: 32px;"
                            />
                          </a>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
            </tbody>
          </table>
        </footer>
      </td>
    </tr>
  </table>
</body>
</html>
