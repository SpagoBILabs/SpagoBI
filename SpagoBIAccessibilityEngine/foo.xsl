<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
              
<xsl:output method="xml" indent="yes" 
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" />

<xsl:template match="/">
  <html xmlns="http://www.w3.org/1999/xhtml">
 	<head>
	  <link href="../main.css" rel="stylesheet" type="text/css" />
	</head>
  <body>
  <h2>Risultato:</h2>
  <table>
    <tr class="verde">
      <th>Regione</th>
      <th>Persone in cerca</th>
      <th>Non forze lavoro</th>
      <th>Occupati</th>
      <th>Totale</th>
    </tr>
    <xsl:for-each select="ROWS/ROW">
    <tr>
      <td><xsl:value-of select="@REGIONE"/></td>
      <td><xsl:value-of select="@INCERCA"/></td>
      <td><xsl:value-of select="@NONLAVORO"/></td>
      <td><xsl:value-of select="@OCCUPATI"/></td>
      <td><xsl:value-of select="@TOTALE"/></td>
    </tr>
    </xsl:for-each>
  </table>
  </body>
  </html>
</xsl:template>

</xsl:stylesheet>