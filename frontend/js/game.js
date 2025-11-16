console.log('Scripts loading');

console.log('Game interface loaded');
console.log('Server URL:', sessionStorage.getItem('serverUrl'));
console.log('Game ID:', sessionStorage.getItem('gameId'));
console.log('User Name:', sessionStorage.getItem('userName'));
console.log('User Id:', sessionStorage.getItem('userId'));

const serverUrl = sessionStorage.getItem('serverUrl');
const gameId = sessionStorage.getItem('gameId');
const userName = sessionStorage.getItem('userName');
const userId = sessionStorage.getItem('userId');

// Fetch the board data from the server
function displayBoard() {
    return fetch(`${serverUrl}${gamePrefix}/game/${gameId}/board`)
    .then(response => response.json())
    .then(drawBoard)
    .catch(error => {
        console.error('Error fetching board data:', error);
    })
    .then(_ => {
        fetch(`${serverUrl}${gamePrefix}/game/${gameId}/meeples?userId=${userId}`)
        .then(response => response.json())
        .then(drawMeeples)
        .catch(error => {
            console.error('Error fetching meeple data:', error);
        });
    });
}

// Debounce function to limit the rate at which a function is called
function debounce(func, wait) {
    let timeout;
    return function(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), wait);
    };
}

// Function to handle resize events
async function handleResize() {
    try {
        // Await the completion of displayBoard before calling initializePlayerDisplay
        await displayBoard();
        initializePlayerDisplay();
    } catch (error) {
        console.error('Error during resize event:', error);
    }
}

// Debounced version of handleResize
const debouncedHandleResize = debounce(handleResize, 200);

// Attach resize event listener with debouncing
window.addEventListener('resize', debouncedHandleResize);

// Attach fullscreen change event listeners
document.addEventListener('fullscreenchange', debouncedHandleResize);
document.addEventListener('webkitfullscreenchange', debouncedHandleResize);
document.addEventListener('mozfullscreenchange', debouncedHandleResize);
document.addEventListener('MSFullscreenChange', debouncedHandleResize);

// Initial call to set up the UI correctly on page load
handleResize();


const canvas = document.getElementById('gameBoard');

// Add event listener for click events to get the cell coordinates
canvas.addEventListener('click', (event) => {
    const cellSize = sessionStorage.getItem("cellSize");
    const rect = canvas.getBoundingClientRect();
    const x = (event.clientX - rect.left);
    const y = (event.clientY - rect.top);
    const cellX = Math.floor(x / cellSize);
    const cellY = Math.floor(y / cellSize);


    // Log the click position
    console.log(`Click position: X = ${cellX}, Y = ${cellY}`);



    const selected = sessionStorage.getItem('selectedMeeple');
    const meeplesPos = JSON.parse(sessionStorage.getItem('meeplesPos'));
    const meepleHere = meeplesPos.some(m => m.x === cellX && m.y === cellY);

    if (meepleHere) {
        sessionStorage.setItem('selectedMeeple', JSON.stringify({ x: cellX, y: cellY }));
        fetch(`${serverUrl}${gamePrefix}/game/${gameId}/moves?userId=${userId}&x=${cellX}&y=${cellY}`)
        .then(response => response.json())
        .then(drawMoves)
        .catch(error => {
            console.error('Error Getting moves data:', error);
        });
    } else if (selected !== null) {
        const selectedMeeple = JSON.parse(selected);
        fetch(`${serverUrl}${gamePrefix}/game/${gameId}/move?userId=${userId}&srcX=${selectedMeeple.x}&srcY=${selectedMeeple.y}&dstX=${cellX}&dstY=${cellY}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            displayBoard();
            initializePlayerDisplay()
            sessionStorage.removeItem('selectedMeeple');
        })
        .catch(error => {
            console.error('Error moving meeple:', error);
        });
    }
});

document.getElementById('resetButton').addEventListener('click', () => {
    fetch(`${serverUrl}${gamePrefix}/game/${gameId}/move?userId=${userId}`, {
        method: 'DELETE'
    })
    .then(response => {
        displayBoard()
        .then(initializePlayerDisplay())
    });
});

document.getElementById('submitButton').addEventListener('click', () => {
    fetch(`${serverUrl}${gamePrefix}/game/${gameId}/move/select?userId=${userId}`, {
        method: 'PUT'
    });
});

function updateTimer() {
    fetch(`${serverUrl}${gamePrefix}/game/${gameId}/timer`)
    .then(response => response.json())
    .then(data => {
        const newTime = convertTimeToTimer(data.time);
        if (newTime !== document.getElementById('timer').textContent) {
            initializePlayerDisplay()
            if (!data.running) displayBoard();
        }
        document.getElementById('timer').textContent = newTime;
    })
    .catch(error => {
        console.error('Error fetching timer data:', error);
    });
}

updateTimer();
setInterval(updateTimer, 500);
