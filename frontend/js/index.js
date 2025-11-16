document.getElementById('connectForm').addEventListener('submit', function (event) {
    event.preventDefault(); // Prevent the default form submission

    const gameId = document.getElementById('gameId').value;
    const userName = document.getElementById('userName').value;
    const connectError = document.getElementById('connectError');

    // Clear any previous error message
    connectError.textContent = '';
    console.log(`Connecting to : ${gameId}`);
    // Send a request to join the game
    fetch(`${serverUrl}${gamePrefix}/game/${gameId}/join?name=${userName}`, {
        method: 'POST'
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(errorData => {
                connectError.textContent = errorData || 'Failed to connect to the game';
            });
        }
        return response.json();
    })
    .then(data => {
        if (data !== null) {
            console.log(`Joined game ${gameId}`)
            // Save the backend URL in session storage
            sessionStorage.setItem('serverUrl', serverUrl);


            // Save the player's UUID in local storage
            sessionStorage.setItem('userId', data);
            sessionStorage.setItem('gameId', gameId);
            sessionStorage.setItem('userName', userName);

            // Redirect to game.html with query parameters
            window.location.href = `game.html`;
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while connecting to the game');
    });
});

document.getElementById('createForm').addEventListener('submit', function (event) {
    event.preventDefault(); // Prevent the default form submission

    const gameId = document.getElementById('newRoomName').value;

    // Send a request to create a new game
    fetch(`${serverUrl}${gamePrefix}/game/${gameId}`, {
        method: 'POST'
    })
    .then(response => response.text())
    .then(data => {
        if (data) {
            // Update the list of available games
            fetchGames();
        } else {
            alert('Failed to create a new game');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('An error occurred while creating a new game');
    });
});

document.getElementById('helloButton').addEventListener('click', function () {
    console.log("Hello button clicked");
    const helloResult = document.getElementById('helloResult');
    helloResult.textContent = "Loading...";
    helloResult.style.color = "#666";

    // Send a request to the /mutant-meeple/hello endpoint
    fetch(`${serverUrl}${gamePrefix}/hello`)
    .then(response => {
        if (response.ok) {
            return response.text().then(data => {
                // Display success result
                helloResult.textContent = `✅ Success: ${data}`;
                helloResult.style.color = "green";
                console.log(`Received hello response: ${data}`);
            });
        } else {
            return response.text().then(errorData => {
                // Display error with status code
                helloResult.textContent = `❌ Error (${response.status}): ${errorData || response.statusText}`;
                helloResult.style.color = "red";
            });
        }
    })
    .catch(error => {
        // Display network or other errors
        helloResult.textContent = `❌ Network Error: Server unreachable`;
        helloResult.style.color = "red";
        console.error('Hello request failed:', error);
    });
    console.log("Hello request sent");
});

// Fetch and display the list of available games
function fetchGames() {
    fetch(`${serverUrl}${gamePrefix}/games`)
    .then(response => response.json())
    .then(data => {
        const gamesList = document.getElementById('gamesList');
        gamesList.innerHTML = '';

        data.forEach(game => {
            const listItem = document.createElement('li');
            listItem.textContent = `Game Name: ${game}`;
            gamesList.appendChild(listItem);
        });
    })
    .catch(error => {
        console.error('Error fetching games:', error);
    });
}

document.getElementById('updateGames').addEventListener('click', fetchGames);

// Server URL configuration
let serverUrl = defaultServerUrl; // Use the default from config.js

// Show server configuration popup
function showServerConfigModal(isInitial = false) {
    const modal = document.getElementById('serverConfigModal');
    const serverUrlInput = document.getElementById('serverUrlInput');
    const modalTitle = document.getElementById('modalTitle');

    // Update title based on context
    modalTitle.textContent = isInitial ? 'Initial Server Setup' : 'Change Server Settings';

    // Set current server URL as default
    serverUrlInput.value = serverUrl;

    modal.style.display = 'block';
}

// Handle server configuration form submission
document.getElementById('serverConfigForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const serverUrlInput = document.getElementById('serverUrlInput');
    const newServerUrl = serverUrlInput.value.trim();

    if (newServerUrl) {
        // Remove trailing slash if present
        serverUrl = newServerUrl.replace(/\/$/, '');
        hideServerConfigModal();
        initializePage();
    }
});

// Handle "Use Default" button
document.getElementById('useDefaultUrl').addEventListener('click', function () {
    serverUrl = defaultServerUrl; // Reset to original default
    hideServerConfigModal();
    initializePage();
});

// Hide server configuration modal
function hideServerConfigModal() {
    document.getElementById('serverConfigModal').style.display = 'none';
}

// Handle settings button click
document.getElementById('settingsButton').addEventListener('click', function() {
    showServerConfigModal();
});

// Initialize page after server URL is configured
function initializePage() {
    // Fetch games after configuration
    fetchGames();
}

// Modified fetch functions to use serverUrl
function fetchGames() {
    fetch(`${serverUrl}${gamePrefix}/games`)
    .then(response => response.json())
    .then(data => {
        const gamesList = document.getElementById('gamesList');
        gamesList.innerHTML = '';

        data.forEach(game => {
            const listItem = document.createElement('li');
            listItem.textContent = `Game Name: ${game}`;
            gamesList.appendChild(listItem);
        });
        console.log(`got games: ${data.length}: ${data.join(', ')}`);
    })
    .catch(error => {
        console.error('Error fetching games:', error);
    });
}

// Show popup on page load
// window.onload = function() {
//     showServerConfigModal(true);
// };
