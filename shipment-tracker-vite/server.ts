const express = require('express');
const bodyParser = require('body-parser');
const axios = require('axios');

const app = express();
const PORT = 3000;

app.use(bodyParser.json());

app.post('/api/shipment/updates', async (req, res) => {
    const update = req.body;
    try {
        // Forward the request to the Kotlin backend
        await axios.post('http://localhost:3000/api/shipment/updates', update);
        res.status(200).send('Update forwarded to Kotlin backend');
    } catch (error) {
        console.error('Error forwarding update:', error);
        res.status(500).send('Error forwarding update');
    }
});

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});