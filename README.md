# rad

Sharetribe Radiator

Built with Clojure and Docker with an extremely thin layer of JavaScript in front of them.

## Architecture

### JavaScript front

JavaScript front polls the Clojure backend. Clojure responses with JSON Object that contains the template to use and values to show. The responsibility of the JavaScript front is to render the right template with the provided values.

### Clojure backend

Clojure backend fetches information from 3rd-party APIs and constructs a response that contains the template to use and the values to show.

There is a possibility to add any number of "pages". Currently, there is only one page that shows the number of open unassigned UserVoice tickets.

## Prerequisites

Docker

## Running

The Docker image is based on official Clojure Docker image: https://registry.hub.docker.com/_/clojure/

```bash
docker build -t my-clojure-app .
docker run -it -p 3000:3000 -e "USERVOICE_API_KEY=api-key-here" -e "USERVOICE_API_SECRET=api-secret-here" --rm --name my-running-app my-clojure-app
```
