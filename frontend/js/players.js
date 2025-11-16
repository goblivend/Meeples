/**
 * Fetch player data from the server.
 * @returns {Promise<Object[]>} A promise that resolves to an array of player objects.
 */
async function fetchPlayerData() {
    try {
        const response = await fetch(`${serverUrl}${gamePrefix}/game/${gameId}/players`);
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return await response.json();
    } catch (error) {
        console.error('Failed to fetch player data:', error);
        return [];
    }
}

/**
 * Calculate the size of the meeples based on available space.
 * @param {HTMLElement} playersList - The container for the players list.
 * @param {Object[]} players - The array of player objects.
 * @returns {number} The calculated meeple size.
 */
function calculateMeepleSize(playersList, players) {
    console.log("Players list")
    console.log(playersList)
    const parent = document.getElementById('subcomponents');
    const maxAvailableHeight = parent.clientHeight;

    const meepleSizeWidthDependant = playersList.clientWidth / players[0].meeples.length * 0.8;
    const meepleSizeHeightDependant = maxAvailableHeight / players.length / 5;
    console.log("Client's width");
    console.log(playersList.clientWidth);
    console.log("Width: " + meepleSizeWidthDependant)
    console.log("Height: " + meepleSizeHeightDependant)


    const meepleSize = Math.min(meepleSizeWidthDependant, meepleSizeHeightDependant, 64);
    console.log(meepleSize);
    return meepleSize;
}

/**
 * Create the main player item container.
 * @returns {HTMLElement} The player item container.
 */
function createPlayerItemContainer() {
    const playerItem = document.createElement('div');
    playerItem.className = 'playerItem';
    return playerItem;
}

/**
 * Create and append the player name section.
 * @param {HTMLElement} playerItem - The player item container.
 * @param {string} playerName - The name of the player.
 */
function createPlayerNameSection(playerItem, playerName) {
    const playerNameDiv = document.createElement('div');
    playerNameDiv.className = 'playerNameDiv';
    playerItem.appendChild(playerNameDiv);

    const playerNameConstant = document.createElement('div');
    playerNameConstant.className = 'playerNameConstant';
    playerNameConstant.innerText = 'Player : ';
    playerNameDiv.appendChild(playerNameConstant);

    const playerNameVariable = document.createElement('div');
    playerNameVariable.className = 'playerNameVariable';
    playerNameVariable.innerText = playerName;
    playerNameDiv.appendChild(playerNameVariable);
}

/**
 * Create and append the meeples using section.
 * @param {HTMLElement} playerItem - The player item container.
 * @param {Object[]} meeples - The array of meeple objects.
 * @param {number} meepleSize - The size of the meeples.
 */
function createMeeplesUsingSection(playerItem, meeples, meepleSize) {
    const playerMeeplesUsingDiv = document.createElement('div');
    playerItem.appendChild(playerMeeplesUsingDiv);

    let meepleIdx = 0;
    for (let i = 0; i < 3; i++) {
        const usingMeeplesDiv = document.createElement('div');
        usingMeeplesDiv.className = 'usingMeeple';
        playerMeeplesUsingDiv.appendChild(usingMeeplesDiv);

        const meepleCanvas = document.createElement('canvas');
        meepleCanvas.width = meepleCanvas.height = meepleSize;
        meepleCanvas.className = 'meepleCanvas';
        usingMeeplesDiv.appendChild(meepleCanvas);

        const meepleCtx = meepleCanvas.getContext('2d');
        meepleCtx.clearRect(0, 0, meepleCanvas.width, meepleCanvas.height);

        for (; meepleIdx < meeples.length; meepleIdx++) {
            if (meeples[meepleIdx].count === 0) continue;

            meepleCtx.fillStyle = getMeepleColor(meeples[meepleIdx].meepleType);
            meepleCtx.fillRect(0, 0, meepleSize, meepleSize);
            meepleIdx++
            break;
        }
    }
}

/**
 * Create and append the meeples list section.
 * @param {HTMLElement} playerItem - The player item container.
 * @param {Object[]} meeples - The array of meeple objects.
 * @param {number} meepleSize - The size of the meeples.
 */
function createMeeplesListSection(playerItem, meeples, meepleSize) {
    const playerMeeplesDiv = document.createElement('div');
    playerMeeplesDiv.className = 'playerMeeples';
    playerItem.appendChild(playerMeeplesDiv);

    meeples.forEach(meeple => {
        const meepleCanvas = document.createElement('canvas');
        meepleCanvas.width = meepleCanvas.height = meepleSize;
        meepleCanvas.className = 'meepleCanvas';
        playerMeeplesDiv.appendChild(meepleCanvas);

        const meepleCtx = meepleCanvas.getContext('2d');
        meepleCtx.clearRect(0, 0, meepleCanvas.width, meepleCanvas.height);

        meepleCtx.fillStyle = getMeepleColor(meeple.meepleType);
        meepleCtx.fillRect(0, 0, meepleSize, meepleSize);
        if (meeple.used) {
            meepleCtx.strokeStyle = "rgb(0, 0, 0)";
            drawPolygon(meepleCtx, createCheckMark(meepleSize), "rgb(107, 253, 107)");
        }
    });
}

/**
 * Create a player item element with all its subcomponents.
 * @param {Object} player - The player object.
 * @param {number} meepleSize - The size of the meeples.
 * @returns {HTMLElement} The player item element.
 */
function createPlayerItem(player, meepleSize) {
    const playerItem = createPlayerItemContainer();
    createPlayerNameSection(playerItem, player.name);
    createMeeplesUsingSection(playerItem, player.meeples, meepleSize);
    createMeeplesListSection(playerItem, player.meeples, meepleSize);
    return playerItem;
}

/**
 * Set the players list in the DOM.
 * @param {Object[]} players - The array of player objects.
 */
async function setPlayersList(players) {
    const playersList = document.getElementById('players');
    playersList.replaceChildren();

    const meepleSize = calculateMeepleSize(playersList, players);

    players.forEach(player => {
        const playerItem = createPlayerItem(player, meepleSize);
        playersList.appendChild(playerItem);
    });
}

/**
 * Initialize the player display.
 */
async function initializePlayerDisplay() {
    const players = await fetchPlayerData();
    setPlayersList(players);
}
