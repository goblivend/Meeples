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
    document.getElementById('helloResult').textContent = "";

    // Send a request to the /mutant-meeple/hello endpoint
    fetch(`${serverUrl}${gamePrefix}/hello`)
    .then(response => response.text())
    .then(data => {
        // Display the result in the helloResult paragraph
        document.getElementById('helloResult').textContent = data;
    })
    .catch(() => {
        document.getElementById('helloResult').textContent = "Server unreachable";
    });
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

// Fetch games on page load
window.onload = fetchGames;
