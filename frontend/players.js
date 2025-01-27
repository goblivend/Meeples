function fetchPlayers() {
    return fetch(`${serverUrl}${gamePrefix}/game/${gameId}/players`)
            .then(response => response.json())
}

function setPlayers(players) {
    console.log(players)

    const playersList = document.getElementById('players')

    playersList.replaceChildren();


    const meepleSizeWidthDependant = playersList.clientWidth / players[0].meeples.length * 8 / 10;

    const parent = document.getElementById('subcomponents');
    const maxAvailableHeight = parent.offsetHeight;
    console.log(maxAvailableHeight);


    const meepleSizeHeightDependant = maxAvailableHeight / players.length / 5;



    const meepleSize = 64;// Math.min(meepleSizeWidthDependant, meepleSizeHeightDependant);

    console.log(`Width Dependant: ${meepleSizeWidthDependant}, Height Dependant: ${meepleSizeHeightDependant}, Meeple Size: ${meepleSize}`);

    players.forEach(player => {
        const playerItem = document.createElement('div');
        playerItem.className = 'playerItem';
        playersList.appendChild(playerItem);


        const playerNameDiv = document.createElement('div');
        playerNameDiv.className = 'playerNameDiv';
        playerItem.appendChild(playerNameDiv);

        const playerNameConstant = document.createElement('div');
        playerNameConstant.className = 'playerNameConstant';
        playerNameConstant.innerText = 'Player : ';
        playerNameDiv.appendChild(playerNameConstant);

        const playerNameVariable = document.createElement('div');
        playerNameVariable.className = 'playerNameVariable';
        playerNameVariable.innerText = `${player.name}`;
        playerNameDiv.appendChild(playerNameVariable);

        const playerMeeplesUsingDiv = document.createElement('div');
        playerItem.appendChild(playerMeeplesUsingDiv);

        let meepleIdx = 0;
        for (let i = 0; i < 3; i++) {
            const usingMeeplesDiv = document.createElement('div');
            usingMeeplesDiv.className = 'usingMeeple';
            playerItem.appendChild(usingMeeplesDiv);

            const meepleCanvas = document.createElement('canvas');
            meepleCanvas.width = meepleSize;
            meepleCanvas.height = meepleSize;
            meepleCanvas.className = 'meepleCanvas';
            usingMeeplesDiv.appendChild(meepleCanvas);

            const meepleCtx = meepleCanvas.getContext('2d');

            for (; meepleIdx < player.meeples.length; meepleIdx++) {
                if (player.meeples[meepleIdx].count === 0)
                    continue;

                meepleCtx.fillStyle = getMeepleColor(player.meeples[meepleIdx++].meepleType);
                meepleCtx.fillRect(0, 0, meepleSize, meepleSize);
                break;
            }
        }


        const playerMeeplesDiv = document.createElement('div');
        playerMeeplesDiv.className = 'playerMeeples';
        playerItem.appendChild(playerMeeplesDiv);

        console.log(meepleSize);
        console.log('######### #############')
        console.log(player)
        console.log('######### #############')

        player.meeples.forEach(meeple => {
            console.log(meeple)
            const meepleCanvas = document.createElement('canvas');
            meepleCanvas.width = meepleSize;
            meepleCanvas.height = meepleSize;
            meepleCanvas.className = 'meepleCanvas';
            playerMeeplesDiv.appendChild(meepleCanvas);

            const meepleCtx = meepleCanvas.getContext('2d');

            meepleCtx.fillStyle = getMeepleColor(meeple.meepleType);
            meepleCtx.fillRect(0, 0, meepleSize, meepleSize);
            if (meeple.used) {
                // Draw a polygon to indicate meeple in super team
                meepleCtx.strokeStyle = "rgb(0, 0, 0)"
                drawPolygon(meepleCtx, createCheckMark(meepleSize), "rgb(107, 253, 107)")

            }
        })
    })

}
