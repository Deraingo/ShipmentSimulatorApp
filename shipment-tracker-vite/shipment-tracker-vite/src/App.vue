<template>
  <div id="app">
    <h1>Shipment Tracker</h1>
    <input v-model="inputText" placeholder="Enter shipment update command" />
    <button @click="sendUpdate">Send Update</button>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import axios from 'axios';

export default defineComponent({
  name: 'App',
  setup() {
    const inputText = ref('');

  const sendUpdate = async () => {
    const [command, ...args] = inputText.value.split(',');
    const update = parseUpdateCommand(command, args);
    console.log('Parsed update:', update);  // Log the update object

    try {
      await axios.post('http://localhost:3000/api/shipment/updates', update);
      console.log('Update sent:', update);
    } catch (error) {
      console.error('Error sending update:', error);
    }
  };

  const parseUpdateCommand = (command: string, args: string[]): any => {
    switch (command.trim()) {
      case 'created':
        return {
          type: 'created',
          id: args[0]?.trim() || '',
          timestamp: parseInt(args[1]?.trim() || '0', 10)
        };
      case 'shipped':
        return {
          type: 'shipped',
          id: args[0]?.trim() || '',
          timestamp: parseInt(args[1]?.trim() || '0', 10),
          expectedDeliveryTimestamp: parseInt(args[2]?.trim() || '0', 10)
        };
      case 'location':
        return {
          type: 'location',
          id: args[0]?.trim() || '',
          timestamp: parseInt(args[1]?.trim() || '0', 10),
          location: args[2]?.trim() || ''
        };
      case 'delayed':
        return {
          type: 'delayed',
          id: args[0]?.trim() || '',
          timestamp: parseInt(args[1]?.trim() || '0', 10),
          expectedDeliveryTimestamp: parseInt(args[2]?.trim() || '0', 10)
        };
      case 'noteadded':
        return {
          type: 'noteadded',
          id: args[0]?.trim() || '',
          timestamp: parseInt(args[1]?.trim() || '0', 10),
          note: args.slice(2).join(',').trim() || ''
        };
      default:
        return {};
    }
  };

    return {
      inputText,
      sendUpdate,
    };
  },
});
</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>