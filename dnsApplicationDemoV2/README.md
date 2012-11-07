dnsApplicationDemo Recipe Notes

This version of the DNS scaling demo has the following changes/enhancements:
- It includes a third VM called the DNS Load Generator Service. This is a web service from which the user can cause load to be generated and see the new VM being created and destroyed.
- Scaling is based on a custom monitor that uses the "rndc stats" capability to get Bind-related statistics. Specifically, it looks at the DNS request rate.
