---
date: 2012-03-21
title: Ever wanted to programmatically file a lawsuit? In Poland, you can.
categories: programming
---

This has somehow escaped me: just over a year ago, the Sixth Civil Division of the Lublin-West Regional Court in Lublin, Poland, has opened its [online branch][1]. It serves the entire territory of Poland and is competent to recognize lawsuits concerning payment claims. There is [basic information][2] available in English. It has proven immensely popular, having processed about two million cases in its first year of operation.

And the really cool thing is, _they have an API_.

It’s SOAP-based and has a [publicly available spec][3]. (Due to the way their web site is constructed, I cannot link to the spec directly; this last link leads to a collection of files related to the web service. The spec is called `EpuWS_ver.1.14.1.pdf`; it’s in Polish only, but it should be easy to run it through Google Translate.) There are a couple of XML schemas as well, plus the spec contains links to a WSDL and some code samples (in C#) at the end.

To actually use the API, you need to get yourself an account of the appropriate type (there are two types corresponding to two groups of methods one can use: that of a bailiff and of a mass plaintiff). You then log on to the system, where you can create an API key that is later used for authentication. They throttle the speed down to 1 req/s per user to mitigate DoS attacks.

The methods include `FileLawsuits`, `FileComplaints`, `SupplyDocuments`, `GetCaseHistory` and so on (the actual names are in Polish). To give you an example, the `FileLawsuits` method returns a structure that consists of, _inter alia_, the amount of court fee to pay, the value of the matter of dispute (both broken down into individual lawsuits), and a status code with a description.

iOS app, anyone?

 [1]: https://www.e-sad.gov.pl/
 [2]: https://www.e-sad.gov.pl/Subpage.aspx?page_id=35
 [3]: https://www.e-sad.gov.pl/Subpage.aspx?page_id=32
